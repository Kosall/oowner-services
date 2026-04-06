package com.piseth.java.school.ownerservice.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.piseth.java.school.ownerservice.enumeration.OwnerStatus;

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
@Table("owners")
public class Owner {

    @Id
    private UUID id;          // keep null if DB generates it

    private String email;

    private String phone;

    private OwnerStatus status;
    
    private Instant emailVerifiedAt;

    private Instant phoneVerifiedAt;

    private Instant createdAt; // created_at

    private Instant updatedAt;
}
