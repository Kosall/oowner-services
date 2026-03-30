package com.piseth.java.school.ownerservice.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.piseth.java.school.ownerservice.domain.Owner;

import reactor.core.publisher.Mono;

public interface OwnerRepository extends ReactiveCrudRepository<Owner, UUID> {

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByPhone(String phone);
}