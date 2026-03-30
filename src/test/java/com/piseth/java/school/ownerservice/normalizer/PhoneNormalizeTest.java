package com.piseth.java.school.ownerservice.normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class PhoneNormalizeTest {
	
	PhoneNormalizer normalizer=new PhoneNormalizer();
	@Test
	void phoneNormalizeTest() {
		assertNull(normalizer.normalize(null));
	}
	
	@Test
	void returnNull_whenPhoneIsBlank() {
		assertNull(normalizer.normalize(""));
		assertNull(normalizer.normalize(" "));
		assertNull(normalizer.normalize("  "));
	}
	
	@Test
	void returnValue_whenPhoneIsNotBlankd() {
		String phone ="012988877";
		String normalize = normalizer.normalize(phone);
		assertEquals("012988877", normalize);
		
	}
	@Test
	void returnValue_whenPhoneTrim() {
		String phone =" 012 988 877 ";
		String normalize = normalizer.normalize(phone);
		assertEquals("012 988 877", normalize);
		
	}
	


}
