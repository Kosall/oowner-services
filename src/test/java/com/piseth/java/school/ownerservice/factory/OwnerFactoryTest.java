package com.piseth.java.school.ownerservice.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.enumeration.OwnerStatus;

public class OwnerFactoryTest {
	
	 @Test
	    void newPendingOwner_shouldCreateNewOwnerWithPendingStatusAndAuditFields_andNotMutateDraft() {
	        // Given: fixed time for deterministic test
	        Instant fixedNow = Instant.parse("2026-01-01T00:00:00Z");
	        Clock fixedClock = Clock.fixed(fixedNow, ZoneOffset.UTC);
	        OwnerFactory factory=new OwnerFactory(fixedClock);
	        UUID draftId=UUID.randomUUID();
	        Owner draft=Owner.builder()
	        		.id(draftId)
	        		.email("temple@example.com")
	        		.phone("096 638 5277")
	        		.status(OwnerStatus.ACTIVE)
	        		.createdAt(Instant.parse("2025-01-01T00:00:00Z"))
	        		.updatedAt(Instant.parse("2025-01-02T00:00:00Z"))
	        		.build();
	        
	        
	        Owner result=factory.newPendingOwner(draft);
	        assertNotSame(draft, result);
	        assertEquals(draftId, result.getId());
	        assertEquals("temple@example.com", result.getEmail());
	        assertEquals("096 638 5277", result.getPhone());
	        assertEquals(OwnerStatus.PENDING, result.getStatus());
	        assertEquals(fixedNow ,  result.getCreatedAt());
	        assertEquals(fixedNow ,  result.getUpdatedAt());
	     // Assert: draft object not modified
	        assertEquals(OwnerStatus.ACTIVE, draft.getStatus());
	        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), draft.getCreatedAt());
	        assertEquals(Instant.parse("2025-01-02T00:00:00Z"), draft.getUpdatedAt());
	        
	 }

}
