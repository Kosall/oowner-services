package com.piseth.java.school.ownerservice.notification;

import org.springframework.stereotype.Component;


import com.piseth.java.school.ownerservice.enumeration.VerificationType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Component
@Slf4j

public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public Mono<Void> send(String target, VerificationType type, String otp) {
       
        return Mono.fromRunnable( ()->log.info("Console OTP sent. type={}, target={}, otp={}", type, target, otp));
    }
}