package com.piseth.java.school.ownerservice.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.piseth.java.school.ownerservice.domain.Verification;
import com.piseth.java.school.ownerservice.enumeration.VerificationStatus;
import com.piseth.java.school.ownerservice.enumeration.VerificationType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VerificationRepository extends ReactiveCrudRepository<Verification, UUID> {

/*	Mono<Verification> findFirstByOwnerIdAndTypeAndVerifiedFalseOrderByCreatedAtDesc(UUID ownerId,
			VerificationType type);

	Flux<Verification> findByOwnerIdAndTypeAndVerifiedFalse(UUID ownerId, VerificationType type);
	
	@Query("""
		    SELECT *
		    FROM owner_verifications
		    WHERE owner_id = :ownerId
		      AND type = :type
		      AND verified = false
		    ORDER BY created_at DESC
		    LIMIT 1
		""")
		Mono<Verification> findLatestActiveVerification(UUID ownerId, VerificationType type);

		@Query("""
		    SELECT *
		    FROM owner_verifications
		    WHERE owner_id = :ownerId
		      AND type = :type
		      AND verified = false
		""")
		Flux<Verification> findAllActiveVerifications(UUID ownerId, VerificationType type);*/
	Mono<Verification> findFirstByOwnerIdAndTypeAndStatusOrderByCreatedAtDesc(
	        UUID ownerId,
	        VerificationType type,
	        VerificationStatus status
	    );

	    Flux<Verification> findByOwnerIdAndTypeAndStatus(
	        UUID ownerId,
	        VerificationType type,
	        VerificationStatus status
	    );

	    @Query("""
	        SELECT *
	        FROM owner_verifications
	        WHERE owner_id = :ownerId
	          AND type = :type
	          AND status = 'ACTIVE'
	        ORDER BY created_at DESC
	        LIMIT 1
	    """)
	    Mono<Verification> findLatestActiveVerification(UUID ownerId, VerificationType type);
	    
	    @Query("""
	            SELECT *
	            FROM owner_verifications
	            WHERE owner_id = :ownerId
	              AND type = :type
	              AND status = 'ACTIVE'
	        """)
	    Flux<Verification> findAllActiveVerifications(UUID ownerId, VerificationType type);


	}
