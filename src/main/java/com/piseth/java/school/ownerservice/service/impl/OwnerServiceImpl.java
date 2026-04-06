package com.piseth.java.school.ownerservice.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import com.piseth.java.school.ownerservice.enumeration.VerificationType;
import com.piseth.java.school.ownerservice.exceptions.OwnerNotFoundException;
import com.piseth.java.school.ownerservice.factory.OwnerFactory;
import com.piseth.java.school.ownerservice.mapper.OwnerMapper;
import com.piseth.java.school.ownerservice.normalizer.OwnerRegisterRequestNormalizer;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.service.OwnerService;
import com.piseth.java.school.ownerservice.service.VerificationService;
import com.piseth.java.school.ownerservice.validations.OwnerRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {
	private final OwnerRepository ownerRepository;
	private final OwnerMapper ownerMapper;
	private final OwnerFactory ownerFactory;
	private final OwnerRegistrationValidator registrationValidator;
	private final OwnerRegisterRequestNormalizer normalizer;
	private final VerificationService verificationService;

	@Override
	public Mono<OwnerResponse> register(OwnerRegisterRequest request) {

		OwnerRegisterRequest normalized = normalizer.normalize(request);

		Owner draft = ownerMapper.toOwnerDraft(normalized);
		Owner pending = ownerFactory.newPendingOwner(draft);

		return registrationValidator.validate(normalized).then(Mono.defer(() -> ownerRepository.save(pending)))
				.flatMap(savedOwner->sendOtpForAvailableChannels(savedOwner).thenReturn(savedOwner))

//            .then(Mono.defer(()->ownerRepository.save(pending)))
				.doOnSuccess(saved -> log.info("Owner registered successfully. ownerId={}", saved.getId()))
				.map(ownerMapper::toResponse);
	}

	@Override
	public Mono<OwnerResponse> getById(UUID id) {
		// TODO Auto-generated method stub
		return ownerRepository.findById(id).switchIfEmpty(Mono.error(new OwnerNotFoundException(id)))
				.map(ownerMapper::toResponse);
	}
	private Mono<Void> sendOtpForAvailableChannels(Owner owner) {
        Mono<Void> emailOtpMono = Mono.empty();
        Mono<Void> phoneOtpMono = Mono.empty();

        if (StringUtils.hasText(owner.getEmail())) {
            emailOtpMono = verificationService.sendOtp(owner.getId(), VerificationType.EMAIL);
        }

        if (StringUtils.hasText(owner.getPhone())) {
            phoneOtpMono = verificationService.sendOtp(owner.getId(), VerificationType.PHONE);
        }

        return emailOtpMono.then(phoneOtpMono);
    }

}
