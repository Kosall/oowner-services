package com.piseth.java.school.ownerservice.factory;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.enumeration.OwnerStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OwnerFactory {

    /*
     * Clock is injected instead of using Instant.now() directly.
     *
     * WHY?
     * - Improves testability.
     * - In unit tests, we can inject Clock.fixed(...) to control time.
     * - Avoids hidden dependency on system time.
     *
     * This is a senior-level best practice.
     */
    private final Clock clock;

    /**
     * Create a new Owner aggregate in PENDING state.
     *
     * POINTS:
     *
     * Factory Pattern
     *    This class is responsible for creating valid Owner aggregates.
     *    Creation rules should NOT live in controller or service layer.
     *
     * Explicit State Transition
     *    We are transforming a draft Owner into a PENDING Owner.
     *    Draft → PendingOwner
     *
     * Avoid Mutating Input Object
     *    Instead of modifying the draft object, we create a new instance.
     *    This prevents side effects and keeps logic predictable.
     *
     * Business Rule Enforcement
     *    All new Owners must start with status = PENDING.
     *    This is domain logic, not controller logic.
     *
     * Audit Fields Initialization
     *    On creation:
     *      createdAt = now
     *      updatedAt = now
     */
    public Owner newPendingOwner(Owner draft) {

        Instant now = Instant.now(clock);

        return Owner.builder()
            .id(draft.getId()) // usually null; DB generates the ID
            .email(draft.getEmail())
            .phone(draft.getPhone())
            .status(OwnerStatus.PENDING) // enforce lifecycle rule
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    /*
     * ===============================
     * ENTERPRISE DESIGN PRINCIPLES
     * ===============================
     *
     *  Single Responsibility Principle (SRP)
     *   - This class ONLY handles aggregate creation.
     *
     *  Clean Architecture
     *   - Controller handles HTTP
     *   - Service orchestrates flow
     *   - Factory handles creation logic
     *   - Repository handles persistence
     *
     *  Predictability
     *   - No mutation of existing objects.
     *
     *  Testability
     *   - Time source is injectable.
     *
     *  Lifecycle Integrity
     *   - Owner must start in PENDING state.
     */
}