package com.piseth.java.school.ownerservice.exceptions;

import java.util.UUID;

public class OwnerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public OwnerNotFoundException(UUID ownerId) {
        super("Owner not found: " + ownerId);
    }
}