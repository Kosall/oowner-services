package com.piseth.java.school.ownerservice.service;

import java.util.UUID;

import com.piseth.java.school.ownerservice.enumeration.VerificationType;

import reactor.core.publisher.Mono;

public interface VerificationService {

    Mono<Void> sendOtp(UUID ownerId, VerificationType type);

    Mono<Void> verifyOtp(UUID ownerId, VerificationType type, String otp);
}