package com.piseth.java.school.ownerservice.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.piseth.java.school.ownerservice.enumeration.VerificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

//@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class GmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;

    @Override
    public Mono<Void> send(String target, VerificationType type, String otp) {
        if (!VerificationType.EMAIL.equals(type)) {
            log.info("Skip email sending because verification type is not EMAIL. type={}, target={}", type, target);
            return Mono.empty();
        }

        return Mono.fromRunnable(() -> {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(target);
                message.setSubject("Your OTP Code");
                message.setText(buildEmailBody(otp));

                mailSender.send(message);
            })
            .subscribeOn(Schedulers.boundedElastic())
            .doOnSuccess(unused -> log.info("OTP email sent successfully. target={}", target))
            .doOnError(error -> log.error("Failed to send OTP email. target={}", target, error))
            .then();
    }

    private String buildEmailBody(String otp) {
        return """
            Hello,

            Your OTP code is: %s

            This code will expire soon.
            Please do not share this code with anyone.

            Thank you.
            """.formatted(otp);
    }
}