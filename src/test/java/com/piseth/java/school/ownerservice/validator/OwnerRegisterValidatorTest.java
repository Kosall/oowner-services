package com.piseth.java.school.ownerservice.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.exceptions.BadRequestException;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.validations.OwnerRegistrationValidator;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class OwnerRegisterValidatorTest {
	@Mock
	private OwnerRepository ownerRepository;
	
	@InjectMocks
	private OwnerRegistrationValidator ownerRegistrationValidator;

//	private OwnerRegistrationValidator validator() {
//
//		return new OwnerRegistrationValidator(ownerRepository);
//	}

	private static OwnerRegisterRequest req(String email, String phone) {

		OwnerRegisterRequest r = new OwnerRegisterRequest();
		r.setEmail(email);
		r.setPhone(phone);
		return r;

	}
	
	@Nested
	class ValidateRequiredContact {
		@Test
		void shouldErrorSynchronously_whenBothEmailAndPhoneMissing() {
			OwnerRegisterRequest request = req(null, "");
			assertThrows(BadRequestException.class, () -> ownerRegistrationValidator.validate(request));
			verifyNoInteractions(ownerRepository);
		}

		@Test
		void shouldPassRequiredContact_whenItProvided() {
			OwnerRegisterRequest request = req("nita@gmail.com", null);

			when(ownerRepository.existsByEmail("nita@gmail.com")).thenReturn(Mono.just(false));
			StepVerifier.create(ownerRegistrationValidator.validate(request)).verifyComplete();

			verify(ownerRepository).existsByEmail("nita@gmail.com");
			verify(ownerRepository, never()).existsByPhone(any());
			verifyNoMoreInteractions(ownerRepository);

		}

		@Test
		void shouldPassRequiredContact_whenPhoneProvided() {
			OwnerRegisterRequest input = req(null, "0126766633");

			when(ownerRepository.existsByPhone("0126766633")).thenReturn(Mono.just(false));
			StepVerifier.create(ownerRegistrationValidator.validate(input)).verifyComplete();
			verify(ownerRepository).existsByPhone("0126766633");
			verify(ownerRepository, never()).existsByEmail(any());
			verifyNoMoreInteractions(ownerRepository);
		}
	}

	@Nested
	class Uniqueness{
//		@BeforeEach
//		void setup() {
//		    when(ownerRepository.existsByEmail(anyString()))
//		        .thenReturn(Mono.just(false));
//
//		    when(ownerRepository.existsByPhone(anyString()))
//		        .thenReturn(Mono.just(false));
//		}
		@Test
		void shouldComplete_whenEmailAndPhoneAreUnique() {
			OwnerRegisterRequest r=req("temple@gmail.com", "011567892");
			
			when(ownerRepository.existsByPhone("011567892")).thenReturn(Mono.just(false));
			when(ownerRepository.existsByEmail("temple@gmail.com")).thenReturn(Mono.just(false));
			
			StepVerifier.create(ownerRegistrationValidator.validate(r)).verifyComplete();
			InOrder inOrder=Mockito.inOrder(ownerRepository);
			inOrder.verify(ownerRepository).existsByEmail("temple@gmail.com");
			inOrder.verify(ownerRepository).existsByPhone("011567892");
			
			verifyNoMoreInteractions(ownerRepository);
			
		}
		 @Test
         void shouldError_whenEmailAlreadyRegistered_andShouldNotCheckPhone() {
             // Given
             OwnerRegisterRequest request = req("temple@example.com", "012 345");

             // When
             when(ownerRepository.existsByEmail("temple@example.com")).thenReturn(Mono.just(true));
//             when(ownerRepository.existsByPhone("012 345")).thenReturn(Mono.just(true));
             // Then
             StepVerifier.create(ownerRegistrationValidator.validate(request))
                 .expectErrorSatisfies(ex -> {
                     assertTrue(ex instanceof BadRequestException);
                     assertEquals("Email already registered.", ex.getMessage());
                 })
                 .verify();

             // Critical rule: phone check must NOT run if email fails
             verify(ownerRepository).existsByEmail("temple@example.com");
             verify(ownerRepository, never()).existsByPhone(any());
             //verifyNoMoreInteractions(ownerRepository);
         }
//    	 @Test
		 void shouldError_whenPhoneAlreadyRegistered() {
			 //Given
			 OwnerRegisterRequest request= req("temple@gmail.com","duplicate-phone");
			 
			 //when
			 when(ownerRepository.existsByEmail("temple@gmail.com")).thenReturn(Mono.just(false));
			 when(ownerRepository.existsByPhone("duplicate-phone")).thenReturn(Mono.just(true));
			 
			 //Then
			 StepVerifier.create(ownerRegistrationValidator.validate(request)).expectErrorSatisfies(ex->{
				 assertTrue(ex instanceof BadRequestException);
				 assertEquals("Phone already registered.", ex.getMessage());
			 }).verify();
			 
			 verify(ownerRepository).existsByEmail("temple@gmail.com");
			 verify(ownerRepository).existsByPhone("duplicate-phone");
//			 verifyNoMoreInteractions(ownerRepository);
	}
    	 @Test
         void shouldError_whenPhoneAlreadyRegister() {
             // Given
             OwnerRegisterRequest request = req("ok@example.com", "dup-phone");

             // When
             when(ownerRepository.existsByEmail("ok@example.com")).thenReturn(Mono.just(false));
             when(ownerRepository.existsByPhone("dup-phone")).thenReturn(Mono.just(true));

             // Then
             StepVerifier.create(ownerRegistrationValidator.validate(request))
                 .expectErrorSatisfies(ex -> {
                     assertTrue(ex instanceof BadRequestException);
                     assertEquals("Phone already registered.", ex.getMessage());
                 })
                 .verify();

             verify(ownerRepository).existsByEmail("ok@example.com");
             verify(ownerRepository).existsByPhone("dup-phone");
             //verifyNoMoreInteractions(ownerRepository);
         }
    	 @Test
    	 void shouldSkipEmail_whenItBlankOrNullAndOnlyCheckPhone() {
    		 // Given
             OwnerRegisterRequest request = req(null, "012 345");
             
             //when
             when(ownerRepository.existsByPhone("012 345")).thenReturn(Mono.just(false));
             
             //then
             StepVerifier.create(ownerRegistrationValidator.validate(request)).verifyComplete();
             
             verify(ownerRepository,never()).existsByEmail(any());
             verify(ownerRepository).existsByPhone("012 345");
    		 
    	 }
    	 @Test
    	void shouldPropogateRepository(){
    		 // Given
             OwnerRegisterRequest request = req("temple@example.com",null);
             //when
            RuntimeException ex=new  RuntimeException("db-down");
            when(ownerRepository.existsByEmail("temple@example.com")).thenReturn(Mono.error( ex));
            //then
            StepVerifier.create(ownerRegistrationValidator.validate(request))
            			.expectErrorSatisfies(x->assertSame(x, ex))
            			.verify();
            
            verify(ownerRepository).existsByEmail("temple@example.com");
    		 
    	 }
	
	}	
}
