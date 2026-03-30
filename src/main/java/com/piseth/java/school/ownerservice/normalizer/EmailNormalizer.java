package com.piseth.java.school.ownerservice.normalizer;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmailNormalizer {

    public String normalize(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return email.trim().toLowerCase();
    }
}
