package com.piseth.java.school.ownerservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import com.piseth.java.school.ownerservice.enumeration.OwnerStatus;
import com.piseth.java.school.ownerservice.factory.OwnerFactory;
import com.piseth.java.school.ownerservice.mapper.OwnerMapper;
import com.piseth.java.school.ownerservice.normalizer.OwnerRegisterRequestNormalizer;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.validations.OwnerRegistrationValidator;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceImplTest {
	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private OwnerMapper ownerMapper;

	@Mock
	private OwnerFactory ownerFactory;

	@Mock
	private OwnerRegistrationValidator registrationValidator;

	@Mock
	private OwnerRegisterRequestNormalizer normalizer;

	@InjectMocks
	private OwnerServiceImpl ownerService;

	@Nested
	class register {
		@Test
		void shouldNormalizeValidateSaveAndReturnResponse_withRealFieldAssertions() {
			// Arrange
			OwnerRegisterRequest request = new OwnerRegisterRequest();
			request.setEmail(" TEMPLE@Gmail.com ");
			request.setPhone(" 012 765 890 ");

			OwnerRegisterRequest normalized = new OwnerRegisterRequest();
			normalized.setEmail("temple@gmail.com");
			normalized.setPhone("012 765 890");

			when(normalizer.normalize(same(request))).thenReturn(normalized);

			Owner draft = Owner.builder().id(null).email(normalized.getEmail()).phone(normalized.getPhone()).build();

			when(ownerMapper.toOwnerDraft(same(normalized))).thenReturn(draft);

			Instant now = Instant.parse("2026-01-01T00:00:00Z");
			Owner pending = Owner.builder().id(null).email(draft.getEmail()).phone(draft.getPhone())
					.status(OwnerStatus.PENDING).createdAt(now).updatedAt(now).build();

			when(ownerFactory.newPendingOwner(same(draft))).thenReturn(pending);
			when(registrationValidator.validate(same(normalized))).thenReturn(Mono.empty());

			UUID savedId = UUID.randomUUID();
			Owner saved = Owner.builder().id(savedId).email(pending.getEmail()).phone(pending.getPhone())
					.status(pending.getStatus()).createdAt(pending.getCreatedAt()).updatedAt(pending.getUpdatedAt())
					.build();
			when(ownerRepository.save(any(Owner.class))).thenReturn(Mono.just(saved));

			OwnerResponse responseExpected = OwnerResponse.builder().id(savedId).email(saved.getEmail())
					.phone(saved.getPhone()).status(saved.getStatus()).createdAt(saved.getCreatedAt())
					.updatedAt(saved.getUpdatedAt()).build();
			when(ownerMapper.toResponse(saved)).thenReturn(responseExpected);
			StepVerifier.create(ownerService.register(request)).assertNext(actual -> {
				assertEquals(responseExpected.getId(), actual.getId());
				assertEquals("temple@gmail.com", actual.getEmail());
				assertEquals("012 765 890", actual.getPhone());
				assertEquals(OwnerStatus.PENDING, actual.getStatus());
				assertEquals(now, actual.getCreatedAt());
				assertEquals(now, actual.getUpdatedAt());

			}).verifyComplete();

			ArgumentCaptor<Owner> ownerCaptor = ArgumentCaptor.forClass(Owner.class);
			verify(ownerRepository).save(ownerCaptor.capture());
			Owner toSave = ownerCaptor.getValue();
			assertEquals("temple@gmail.com", toSave.getEmail());
			assertEquals("012 765 890", toSave.getPhone());
			assertEquals(OwnerStatus.PENDING, toSave.getStatus());
			assertEquals(now, toSave.getCreatedAt());
			assertEquals(now, toSave.getUpdatedAt());

		}
		@Test
        void shouldNotSave_whenValidationFails() {
            // Arrange
            OwnerRegisterRequest request = new OwnerRegisterRequest();
            request.setEmail("TEST@Example.Com");

            OwnerRegisterRequest normalized = new OwnerRegisterRequest();
            normalized.setEmail("test@example.com");

            when(normalizer.normalize(same(request))).thenReturn(normalized);

            Owner draft = Owner.builder().email(normalized.getEmail()).build();
            when(ownerMapper.toOwnerDraft(same(normalized))).thenReturn(draft);

            Owner pending = Owner.builder().email(draft.getEmail()).status(OwnerStatus.PENDING).build();
            when(ownerFactory.newPendingOwner(same(draft))).thenReturn(pending);

            RuntimeException validationError = new RuntimeException("validation failed");
            when(registrationValidator.validate(same(normalized))).thenReturn(Mono.error(validationError));

            // Act + Assert
            StepVerifier.create(ownerService.register(request))
                .expectErrorSatisfies(ex -> assertSame(validationError, ex))
                .verify();

            // critical: no persistence and no response mapping
            verify(ownerRepository, never()).save(any());
            verify(ownerMapper, never()).toResponse(any());

            verify(normalizer).normalize(same(request));
            verify(ownerMapper).toOwnerDraft(same(normalized));
            verify(ownerFactory).newPendingOwner(same(draft));
            verify(registrationValidator).validate(same(normalized));
            //verifyNoMoreInteractions(ownerRepository, ownerMapper, ownerFactory, registrationValidator, normalizer);
        }

	}

}
