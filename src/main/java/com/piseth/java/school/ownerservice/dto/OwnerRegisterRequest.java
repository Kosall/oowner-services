package com.piseth.java.school.ownerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
public class OwnerRegisterRequest {

    @Email(message = "Invalid email format.")
    @Size(max = 80, message = "Email must be <= 80 characters.")
    private String email;

    @Size(max = 30, message = "Phone must be <= 30 characters.")
    private String phone;
}