package com.piseth.java.school.ownerservice.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.piseth.java.school.ownerservice.enumeration.VerificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("owner_verifications")
public class Verification {

    @Id
    private UUID id;

    private UUID ownerId;

    private VerificationType type;

    private String target;

    private String otpHash;

    private String otpSalt;

    private Instant expiresAt;

    private Integer attemptCount;

    private Integer maxAttempts;

    private boolean verified;

    private Instant verifiedAt;

    private Instant createdAt;

    private Instant updatedAt;
}