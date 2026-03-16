package com.piseth.java.school.ownerservice.service;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;

import reactor.core.publisher.Mono;

public interface OwnerService {

    Mono<OwnerResponse> register(OwnerRegisterRequest request);

}