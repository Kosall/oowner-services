package com.piseth.java.school.ownerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OwnerEmailRegisterRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Size(max = 80, message = "Email must be <= 80 characters.")
    private String email;
}