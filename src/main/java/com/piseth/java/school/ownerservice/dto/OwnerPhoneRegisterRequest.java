package com.piseth.java.school.ownerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OwnerPhoneRegisterRequest {

    @NotBlank(message = "Phone is required.")
    @Size(max = 30, message = "Phone must be <= 30 characters.")
    private String phone;
}