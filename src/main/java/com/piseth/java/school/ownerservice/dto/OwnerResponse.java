package com.piseth.java.school.ownerservice.dto;

import java.time.Instant;
import java.util.UUID;

import com.piseth.java.school.ownerservice.enumeration.OwnerStatus;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class OwnerResponse {
	
 private UUID id;
 private String email;
 private String phone;
 private OwnerStatus status;
 private Instant createdAt;
 private Instant updatedAt;
}
