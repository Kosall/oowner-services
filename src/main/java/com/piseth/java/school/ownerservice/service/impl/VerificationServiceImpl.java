package com.piseth.java.school.ownerservice.service.impl;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.piseth.java.school.ownerservice.configurations.VerificationProperties;
import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.domain.Verification;
import com.piseth.java.school.ownerservice.enumeration.OwnerStatus;
import com.piseth.java.school.ownerservice.enumeration.VerificationType;
import com.piseth.java.school.ownerservice.exceptions.BadRequestException;
import com.piseth.java.school.ownerservice.exceptions.OwnerNotFoundException;
import com.piseth.java.school.ownerservice.factory.VerificationFactory;
import com.piseth.java.school.ownerservice.notification.NotificationSender;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.repository.VerificationRepository;
import com.piseth.java.school.ownerservice.service.VerificationService;
import com.piseth.java.school.ownerservice.verification.OtpGenerator;
import com.piseth.java.school.ownerservice.verification.OtpHasher;
import com.piseth.java.school.ownerservice.verification.OtpSaltGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService{
	private final OwnerRepository ownerRepository;
    private final VerificationRepository verificationRepository;
    private final VerificationFactory verificationFactory;
    private final OtpGenerator otpGenerator;
    private final OtpHasher otpHasher;
    private final OtpSaltGenerator otpSaltGenerator;
    private final NotificationSender notificationSender;
    private final VerificationProperties verificationProperties;
    private final Clock clock;
	/*@Override
	public Mono<Void> sendOtp(UUID ownerId, VerificationType type) {
		// TODO Auto-generated method stub
		 return ownerRepository.findById(ownerId)
		            .switchIfEmpty(Mono.error(new OwnerNotFoundException(ownerId)))
		            .flatMap(owner -> {
		                String target = resolveTarget(owner, type);
		                validateAlreadyVerified(owner, type);

		                String otp = otpGenerator.generateNumericOtp(verificationProperties.getOtpLength());
		                String salt = otpSaltGenerator.generate();
		                String hash = otpHasher.hash(otp, salt, verificationProperties.getOtpPepper());

		                Verification verification = verificationFactory.newVerification(
		                    owner.getId(),
		                    type,
		                    target,
		                    hash,
		                    salt
		                );

		                return verificationRepository.findByOwnerIdAndTypeAndVerifiedFalse(ownerId, type)
		                    .flatMap(existing -> {
		                        existing.setVerified(true);
		                        existing.setVerifiedAt(Instant.now(clock));
		                        existing.setUpdatedAt(Instant.now(clock));
		                        return verificationRepository.save(existing);
		                    })
		                    .then(verificationRepository.save(verification))
		                    .doOnSuccess(saved -> {
		                        notificationSender.send(target, type, otp);
		                        log.info("OTP generated and sent. ownerId={}, type={}", ownerId, type);
		                    })
		                    .then();
		            });
	}
*/
    @Override
    public Mono<Void> sendOtp(UUID ownerId, VerificationType type) {
        return ownerRepository.findById(ownerId)
            .switchIfEmpty(Mono.error(new OwnerNotFoundException(ownerId)))
            .flatMap(owner -> {
                String target = resolveTarget(owner, type);
                validateAlreadyVerified(owner, type);

                return verificationRepository.findLatestActiveVerification(ownerId, type)
                    .flatMap(existingVerification ->
                        validateResendCooldown(existingVerification).thenReturn(existingVerification)
                    )
                    .switchIfEmpty(Mono.empty())
                    .then(Mono.defer(() -> createAndSendOtp(ownerId, type, target)));
            });
    }
    private Mono<Void> validateResendCooldown(Verification verification) {
        Instant now = Instant.now(clock);

        Instant allowedTime = verification.getCreatedAt()
            .plusSeconds(verificationProperties.getResendCooldownSeconds());

        if (now.isBefore(allowedTime)) {
            long remainingSeconds = allowedTime.getEpochSecond() - now.getEpochSecond();

            return Mono.error(new BadRequestException(
                "Please wait " + remainingSeconds + " seconds before requesting a new OTP."
            ));
        }

        return Mono.empty();
    }
    private Mono<Void> createAndSendOtp(UUID ownerId, VerificationType type, String target) {
        String otp = otpGenerator.generateNumericOtp(verificationProperties.getOtpLength());
        String salt = otpSaltGenerator.generate();
        String hash = otpHasher.hash(otp, salt, verificationProperties.getOtpPepper());

        Verification verification = verificationFactory.newVerification(
            ownerId,
            type,
            target,
            hash,
            salt
        );

        Instant now = Instant.now(clock);

        return verificationRepository.findAllActiveVerifications(ownerId, type)
            .flatMap(existing -> {
                existing.setVerified(true);
                existing.setVerifiedAt(now);
                existing.setUpdatedAt(now);
                return verificationRepository.save(existing);
            })
            .then(verificationRepository.save(verification))
            .then(Mono.fromRunnable(()->notificationSender.send(target, type, otp)))
            .doOnSuccess(i->log.info("OTP generated and sent. ownerId={}, type={}",ownerId, type))
            .then();
    }

	@Override
	public Mono<Void> verifyOtp(UUID ownerId, VerificationType type, String otp) {
		 return ownerRepository.findById(ownerId)
		            .switchIfEmpty(Mono.error(new OwnerNotFoundException(ownerId)))
		            .flatMap(owner -> verificationRepository
		                .findFirstByOwnerIdAndTypeAndVerifiedFalseOrderByCreatedAtDesc(ownerId, type)
		                .switchIfEmpty(Mono.error(new BadRequestException("Verification code not found. Please request a new OTP.")))
		                .flatMap(verification -> validateAndConsumeOtp(owner, verification, otp, type)));
		    }
	
	private Mono<Void> validateAndConsumeOtp(
	        Owner owner,
	        Verification verification,
	        String otp,
	        VerificationType type
	    ) {
	        Instant now = Instant.now(clock);

	        if (verification.isVerified()) {
	            return Mono.error(new BadRequestException("OTP has already been used."));
	        }

	        if (verification.getExpiresAt().isBefore(now)) {
	            return Mono.error(new BadRequestException("OTP has expired."));
	        }

	        if (verification.getAttemptCount() >= verification.getMaxAttempts()) {
	            return Mono.error(new BadRequestException("OTP maximum attempts exceeded. Please request a new OTP."));
	        }

	        boolean matches = otpHasher.matches(
	            otp,
	            verification.getOtpSalt(),
	            verificationProperties.getOtpPepper(),
	            verification.getOtpHash()
	        );

	        if (!matches) {
	            verification.setAttemptCount(verification.getAttemptCount() + 1);
	            verification.setUpdatedAt(now);

	            return verificationRepository.save(verification)
	                .then(Mono.error(new BadRequestException("Invalid OTP.")));
	        }

	        verification.setVerified(true);
	        verification.setVerifiedAt(now);
	        verification.setUpdatedAt(now);

	        applyVerificationToOwner(owner, type, now);

	        return verificationRepository.save(verification)
	            .then(ownerRepository.save(owner))
	            .doOnSuccess(saved -> log.info("OTP verified successfully. ownerId={}, type={}", owner.getId(), type))
	            .then();
	    }

	    private void applyVerificationToOwner(Owner owner, VerificationType type, Instant now) {
	        if (VerificationType.EMAIL.equals(type)) {
	            owner.setEmailVerifiedAt(now);
	        }

	        if (VerificationType.PHONE.equals(type)) {
	            owner.setPhoneVerifiedAt(now);
	        }

	        if (isOwnerFullyVerified(owner)) {
	            owner.setStatus(OwnerStatus.ACTIVE);
	        }

	        owner.setUpdatedAt(now);
	    }

	    private boolean isOwnerFullyVerified(Owner owner) {
	        boolean emailRequired = StringUtils.hasText(owner.getEmail());
	        boolean phoneRequired = StringUtils.hasText(owner.getPhone());

	        boolean emailOk = !emailRequired || owner.getEmailVerifiedAt() != null;
	        boolean phoneOk = !phoneRequired || owner.getPhoneVerifiedAt() != null;

	        return emailOk && phoneOk;
	    }

	    private String resolveTarget(Owner owner, VerificationType type) {
	        if (VerificationType.EMAIL.equals(type)) {
	            if (!StringUtils.hasText(owner.getEmail())) {
	                throw new BadRequestException("Owner does not have email.");
	            }
	            return owner.getEmail();
	        }

	        if (VerificationType.PHONE.equals(type)) {
	            if (!StringUtils.hasText(owner.getPhone())) {
	                throw new BadRequestException("Owner does not have phone.");
	            }
	            return owner.getPhone();
	        }

	        throw new BadRequestException("Unsupported verification type.");
	    }

	    private void validateAlreadyVerified(Owner owner, VerificationType type) {
	        if (VerificationType.EMAIL.equals(type) && owner.getEmailVerifiedAt() != null) {
	            throw new BadRequestException("Email is already verified.");
	        }

	        if (VerificationType.PHONE.equals(type) && owner.getPhoneVerifiedAt() != null) {
	            throw new BadRequestException("Phone is already verified.");
	        }
	    }
	}


