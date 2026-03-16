package com.piseth.java.school.ownerservice.service.impl;


import org.springframework.stereotype.Service;

import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import com.piseth.java.school.ownerservice.factory.OwnerFactory;
import com.piseth.java.school.ownerservice.mapper.OwnerMapper;
import com.piseth.java.school.ownerservice.normalizer.OwnerRegisterRequestNormalizer;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.service.OwnerService;
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

	@Override
	public Mono<OwnerResponse> register(OwnerRegisterRequest request) {

        OwnerRegisterRequest normalized = normalizer.normalize(request);

        Owner draft = ownerMapper.toOwnerDraft(normalized);
        Owner pending = ownerFactory.newPendingOwner(draft);

        return registrationValidator.validate(normalized)
            .then(ownerRepository.save(pending))
            .doOnSuccess(saved -> log.info("Owner registered successfully. ownerId={}", saved.getId()))
            .map(ownerMapper::toResponse);
    }
	}

