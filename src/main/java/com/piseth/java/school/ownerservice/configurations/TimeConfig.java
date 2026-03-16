package com.piseth.java.school.ownerservice.configurations;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
    
//    @Bean
//    Clock clock() {
//        return Clock.fixed(
//            Instant.parse("2026-01-01T00:00:00Z"),
//            ZoneOffset.UTC
//        );
//    }
}