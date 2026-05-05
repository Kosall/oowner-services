package com.piseth.java.school.ownerservice.notification;

import com.piseth.java.school.ownerservice.enumeration.VerificationType;

import reactor.core.publisher.Mono;

public interface NotificationSender {

//	void send(String target, VerificationType type, String otp);
	Mono<Void> send(String target, VerificationType type, String otp);
	}