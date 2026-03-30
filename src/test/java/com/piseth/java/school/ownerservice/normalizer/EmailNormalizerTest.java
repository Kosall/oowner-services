package com.piseth.java.school.ownerservice.normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class EmailNormalizerTest {
	private EmailNormalizer normalizer =new EmailNormalizer();
	@Test
	void shouldReTurnNull_whenEmailIsNull() {
		String email=null;
		String emailAfterNormalize=normalizer.normalize(email);
		assertNull(emailAfterNormalize);
	}
	
	@Test
	void shouldReTurnNull_whenEmailIsBlank() {
		String email="";
		String emailAfterNormalize=normalizer.normalize(email);
		assertNull(emailAfterNormalize);
		assertNull(normalizer.normalize(" "));
	}
	@Test
	void shouldReturnLowerCase() {
		String email="NIta@gmail.com";
		String normalize = normalizer.normalize(email);
		assertEquals("nita@gmail.com", normalize);
	}
	
	@Test
	void shouldTrimWhiteSpace() {
		String email=" nita@gmail.com  ";
		String normalize = normalizer.normalize(email);
		assertEquals("nita@gmail.com", normalize);
	}
	@Test
	void shouldTrimWhiteSpaceAndConvertToLowerCase() {
		String email=" NiTa@gmaiL.com  ";
		String normalize = normalizer.normalize(email);
		assertEquals("nita@gmail.com", normalize);
	}

}
