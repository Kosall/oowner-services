package com.piseth.java.school.ownerservice.normalizer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
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

}
