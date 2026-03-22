package com.piseth.java.school.ownerservice.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.exceptions.BadRequestException;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.service.OwnerService;
import com.piseth.java.school.ownerservice.validations.OwnerRegistrationValidator;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
@ExtendWith(MockitoExtension.class)
public class OwnerRegisterValidatorTest {
	@Mock
	private OwnerRepository ownerRepository;
	
	private OwnerRegistrationValidator validator() {
		
		return new OwnerRegistrationValidator(ownerRepository);
	}
	
	private static OwnerRegisterRequest req(String email,String phone) {
		
		OwnerRegisterRequest r=new OwnerRegisterRequest();
		r.setEmail(email);
		r.setPhone(phone);
		return r;
		
	}
	@Nested
	class ValidateRequiredContact{
		@Test
		void shouldErrorSynchronously_whenBothEmailAndPhoneMissing(){
			OwnerRegisterRequest request=req(null, "");
			assertThrows(BadRequestException.class, ()->validator().validate(request));
			verifyNoInteractions(ownerRepository);
		}
		@Test
		void shouldPassRequiredContact_whenItProvided(){
			OwnerRegisterRequest request=req("nita@gmail.com", null);
			
			when(ownerRepository.existsByEmail("nita@gmail.com")).thenReturn(Mono.just(false));
			StepVerifier.create(validator().validate(request)).verifyComplete();
			
			verify(ownerRepository).existsByEmail("nita@gmail.com");
			verify(ownerRepository,never()).existsByPhone(any());
			verifyNoMoreInteractions(ownerRepository);
			
		}
		@Test
		void shouldPassRequiredContact_whenPhoneProvided() {
			OwnerRegisterRequest input=req(null, "0126766633");
			
			when(ownerRepository.existsByPhone("0126766633")).thenReturn(Mono.just(false));
			StepVerifier.create(validator().validate(input)).verifyComplete();
			verify(ownerRepository).existsByPhone("0126766633");
			verify(ownerRepository,never()).existsByEmail(any());
			verifyNoMoreInteractions(ownerRepository);
		}
	}
	
	@Nested
	class Uniqueness{
		@Test
		void shouldComplete_whenEmailAndPhoneAreUnique() {
			OwnerRegisterRequest r=req("temple@gmail.com", "011567892");
			
			when(ownerRepository.existsByPhone("011567892")).thenReturn(Mono.just(false));
			when(ownerRepository.existsByEmail("temple@gmail.com")).thenReturn(Mono.just(false));
			
			StepVerifier.create(validator().validate(r)).verifyComplete();
			InOrder inOrder=Mockito.inOrder(ownerRepository);
			inOrder.verify(ownerRepository).existsByEmail("temple@gmail.com");
			inOrder.verify(ownerRepository).existsByPhone("011567892");
			
			verifyNoMoreInteractions(ownerRepository);
			
		}
	}
	
	
}
