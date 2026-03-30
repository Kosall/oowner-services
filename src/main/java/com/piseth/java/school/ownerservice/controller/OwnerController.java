package com.piseth.java.school.ownerservice.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import com.piseth.java.school.ownerservice.service.OwnerService;

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

    

    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OwnerResponse> register(@Valid @RequestBody OwnerRegisterRequest request) {
    	
    	
        return ownerService.register(request);
        		
    }
    @GetMapping("/{ownerId}")
    public Mono<OwnerResponse> getById(@PathVariable UUID ownerId) {
        return ownerService.getById(ownerId);
    }


}