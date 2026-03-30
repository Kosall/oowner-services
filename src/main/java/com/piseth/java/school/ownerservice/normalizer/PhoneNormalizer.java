package com.piseth.java.school.ownerservice.normalizer;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PhoneNormalizer {

    public String normalize(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }

        return phone.trim(); // @TODO detect only  digit
    }
}