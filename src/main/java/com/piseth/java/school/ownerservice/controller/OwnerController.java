package com.piseth.java.school.ownerservice.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import com.piseth.java.school.ownerservice.dto.VerifyOtpRequest;
import com.piseth.java.school.ownerservice.enumeration.VerificationType;
import com.piseth.java.school.ownerservice.service.OwnerService;
import com.piseth.java.school.ownerservice.service.VerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owners")
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;

    private final VerificationService verificationService;

    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OwnerResponse> register(@Valid @RequestBody OwnerRegisterRequest request) {
    	
    	
        return ownerService.register(request);
        		
    }
    @PostMapping("/{ownerId}/email/send-otp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> sendEmailOtp(@PathVariable UUID ownerId) {
        return verificationService.sendOtp(ownerId, VerificationType.EMAIL);
    }
    @PostMapping("/{ownerId}/phone/send-otp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> sendPhoneOtp(@PathVariable UUID ownerId) {
        return verificationService.sendOtp(ownerId, VerificationType.PHONE);
    }
    
    @PostMapping("/{ownerId}/email/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> verifyEmailOtp(
        @PathVariable UUID ownerId,
        @Valid @RequestBody VerifyOtpRequest request
    ) {
        return verificationService.verifyOtp(ownerId, VerificationType.EMAIL, request.getOtp());
    }
    
    @GetMapping("/{ownerId}")
    public Mono<OwnerResponse> getById(@PathVariable UUID ownerId) {
        return ownerService.getById(ownerId);
    }


}