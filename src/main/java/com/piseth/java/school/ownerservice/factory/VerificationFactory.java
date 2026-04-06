package com.piseth.java.school.ownerservice.factory;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.piseth.java.school.ownerservice.configurations.VerificationProperties;
import com.piseth.java.school.ownerservice.domain.Verification;
import com.piseth.java.school.ownerservice.enumeration.VerificationType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VerificationFactory {

    private final Clock clock;
    private final VerificationProperties verificationProperties;

    public Verification newVerification(
        UUID ownerId,
        VerificationType type,
        String target,
        String otpHash,
        String otpSalt
    ) {
        Instant now = Instant.now(clock);

        return Verification.builder()
            .ownerId(ownerId)
            .type(type)
            .target(target)
            .otpHash(otpHash)
            .otpSalt(otpSalt)
            .expiresAt(now.plusSeconds(verificationProperties.getOtpTtlSeconds()))
            .attemptCount(0)
            .maxAttempts(verificationProperties.getMaxAttempts())
            .verified(false)
            .verifiedAt(null)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
}