package com.piseth.java.school.ownerservice.normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
@ExtendWith(MockitoExtension.class)
public class OwnerRequestNormalizerTest {
	
	@Mock
	private  EmailNormalizer emailNormalizer;
	@Mock
	private  PhoneNormalizer phoneNormalizer;
	@InjectMocks
	private OwnerRegisterRequestNormalizer normalizer;
	String em=" kULaap@gmail.com ";
	String tel=" 077 889 864 ";
	@Test
	void normalizeTest() {
		OwnerRegisterRequest input =new OwnerRegisterRequest();
		input.setEmail(" temple@gmail.com ");
		input.setPhone(" 012 588 898 ");
		
		when(emailNormalizer.normalize(" temple@gmail.com ")).thenReturn("temple@gmail.com");
		when(phoneNormalizer.normalize(" 012 588 898 ")).thenReturn("012 588 898");
		
		OwnerRegisterRequest out=normalizer.normalize(input);
		assertNotNull(out);
		assertNotSame(input, out);
		
	}
	@Test
	void shoudReturnNewInstance_andNotMutate() {
		String email=" exAmple@gmail.com";
		String phone=" 012 588 898 ";
		OwnerRegisterRequest input=new OwnerRegisterRequest();
		input.setEmail(email);
		input.setPhone(phone);
		
		when(emailNormalizer.normalize(email)).thenReturn("example@gmail.com");
		when(phoneNormalizer.normalize(phone)).thenReturn("012 588 898");
		OwnerRegisterRequest out =normalizer.normalize(input);
		assertNotNull(out);
		assertNotSame(input, out);
		
		assertEquals("example@gmail.com", out.getEmail());
		assertEquals("012 588 898", out.getPhone());
		verify(emailNormalizer).normalize(" exAmple@gmail.com");
		verify(phoneNormalizer).normalize(" 012 588 898 ");
		verifyNoMoreInteractions(phoneNormalizer);
	}
	@Test
	void shouldHandleNulls_fromNormalizerTest() {
		OwnerRegisterRequest input =new OwnerRegisterRequest();
		input.setEmail("  ");
		input.setPhone(null);
		
		when(emailNormalizer.normalize("  ")).thenReturn(null);
		when(phoneNormalizer.normalize(null)).thenReturn(null);
		
		OwnerRegisterRequest output=normalizer.normalize(input);
		
		assertNotNull(output);
		assertNotSame(input, output);
		
		assertNull(output.getEmail());
		assertNull(output.getPhone());
		
		verify(emailNormalizer).normalize("  ");
		verify(phoneNormalizer).normalize(null);
	
	}
	@Test
	void shouldAllowPartialInput_whenOnlyPhoneProvided(){
		OwnerRegisterRequest inputting=new OwnerRegisterRequest();
		inputting.setEmail("TEMPLE@gmail.com");
		inputting.setPhone(null);
		
		when(emailNormalizer.normalize("TEMPLE@gmail.com")).thenReturn("temple@gmail.com");
		when(phoneNormalizer.normalize(null)).thenReturn(null);
		
		OwnerRegisterRequest out=normalizer.normalize(inputting);
		
		assertNotSame(inputting, out);
		assertEquals("temple@gmail.com", out.getEmail());
		assertNull(out.getPhone());
		verify(emailNormalizer).normalize("TEMPLE@gmail.com");
		verify(phoneNormalizer).normalize(null);
	}
	@Test
	void shouldAllowPartialInput_whenOnlyEmailProvided() {
		
		OwnerRegisterRequest owner=new OwnerRegisterRequest();
		owner.setEmail(em);
		owner.setPhone(null);
		
		when(emailNormalizer.normalize(em)).thenReturn("kulaap@gmail.com");
		when(phoneNormalizer.normalize(null)).thenReturn(null);
		
		OwnerRegisterRequest out=normalizer.normalize(owner);
		assertNotSame(owner, out);
		assertEquals(null, out.getPhone());
		
		verify(emailNormalizer).normalize(em);
		verify(phoneNormalizer).normalize(null);
	}
	
	
	

}
