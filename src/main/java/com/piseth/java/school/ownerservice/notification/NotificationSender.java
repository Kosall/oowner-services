package com.piseth.java.school.ownerservice.notification;

import com.piseth.java.school.ownerservice.enumeration.VerificationType;

public interface NotificationSender {

	void send(String target, VerificationType type, String otp);
}