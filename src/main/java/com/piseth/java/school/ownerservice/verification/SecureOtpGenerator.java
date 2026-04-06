package com.piseth.java.school.ownerservice.verification;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class SecureOtpGenerator implements OtpGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String generateNumericOtp(int length) {
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(SECURE_RANDOM.nextInt(10));
        }

        return builder.toString();
    }
}
