package com.piseth.java.school.ownerservice.notification;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import org.springframework.stereotype.Component;

import com.piseth.java.school.ownerservice.enumeration.VerificationType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public void send(String target, VerificationType type, String otp) {
        log.info("Console OTP sent. type={}, target={}, otp={}", type, target, otp);
    }
}