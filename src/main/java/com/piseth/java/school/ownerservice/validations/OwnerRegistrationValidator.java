package com.piseth.java.school.ownerservice.validations;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.exceptions.BadRequestException;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class OwnerRegistrationValidator {

    private final OwnerRepository ownerRepository;

    /**
     * Entry point for validation.
     *
     * IMPORTANT POINT:
     * - We execute synchronous validation first (CPU logic).
     * - Then we execute asynchronous validation (DB check).
     *
     * Why return Mono<Void>?
     * In reactive programming, validation does not return true/false.
     * Instead:
     *   - Success  = complete signal
     *   - Failure  = error signal
     *
     * Mono<Void> means:
     *   "I only care whether it completes or errors."
     */
    public Mono<Void> validate(OwnerRegisterRequest request) {
        validateRequiredContact(request);   // pure synchronous check
        return validateUniqueness(request); // reactive DB validation
    }

    /**
     * Pure synchronous validation.
     *
     * NOTE:
     * - No database.
     * - No async.
     * - Therefore no Mono needed.
     *
     * Rule:
     * Use normal Java (void) when logic is CPU-only.
     */
    private void validateRequiredContact(OwnerRegisterRequest request) {
        boolean hasEmail = StringUtils.hasText(request.getEmail());
        boolean hasPhone = StringUtils.hasText(request.getPhone());

        if (!hasEmail && !hasPhone) {
            throw new BadRequestException("Either email or phone must be provided.");
        }
    }

    /**
     * Reactive uniqueness validation.
     *
     * NOTE:
     * - Database calls are asynchronous in WebFlux.
     * - Therefore must remain inside reactive pipeline.
     *
     * We chain email check -> then phone check.
     * If email fails, phone check will NOT execute.
     */
    private Mono<Void> validateUniqueness(OwnerRegisterRequest request) {
        return checkEmailUnique(request.getEmail())
            .then(Mono.defer(()->checkPhoneUnique(request.getPhone())));
    }

    /**
     * Check email uniqueness.
     *
     * WHY .then() ?
     *
     * existsByEmail returns Mono<Boolean>.
     * But validation does not care about Boolean value.
     * We only care:
     *   - If exists -> throw error
     *   - If not -> complete successfully
     *
     * .then() discards the Boolean result
     * and converts the stream into Mono<Void>.
     */
    private Mono<Void> checkEmailUnique(String email) {
    	log.info("checking valid email!!");
        if (!StringUtils.hasText(email)) {
            return Mono.empty(); // nothing to validate
        }

        return ownerRepository.existsByEmail(email)
            .filter(Boolean::booleanValue)
            .flatMap(exists ->
                Mono.error(new BadRequestException("Email already registered."))
            )
            .then(); // convert Mono<Boolean> to Mono<Void>
    }

    /**
     * Same logic as email.
     *
     * NOTE:
     * This pattern is reusable for any uniqueness validation.
     */
    private Mono<Void> checkPhoneUnique(String phone) {
    	log.info("checking valid phone!!");
        if (!StringUtils.hasText(phone)) {
            return Mono.empty();
        }

        return ownerRepository.existsByPhone(phone)
            .filter(Boolean::booleanValue)
            .flatMap(exists ->
                Mono.error(new BadRequestException("Phone already registered."))
            )
            .then();
    }

    /*
     * ================================
     * VERY IMPORTANT ENTERPRISE NOTE
     * ================================
     *
     * This validator improves user experience
     * by checking duplicates before saving.
     *
     * HOWEVER:
     * This is NOT enough for real concurrency safety.
     *
     * We MUST also:
     *   - Add UNIQUE constraint in database
     *   - Handle DataIntegrityViolationException
     *
     * Because:
     * Two concurrent requests can pass this check
     * and still insert duplicate without DB constraint.
     *
     * Validator = Early feedback
     * Database constraint = Real protection
     *
     * ================================
     *
     * SOLID Principle:
     * - Single Responsibility:
     *   This class only validates business rules.
     *
     * - Repository handles persistence.
     * - Service orchestrates flow.
     */
}