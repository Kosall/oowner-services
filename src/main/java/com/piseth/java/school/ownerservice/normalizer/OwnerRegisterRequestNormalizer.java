package com.piseth.java.school.ownerservice.normalizer;

import org.springframework.stereotype.Component;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Builder
public class OwnerRegisterRequestNormalizer {

    private final EmailNormalizer emailNormalizer;
    private final PhoneNormalizer phoneNormalizer;

    public OwnerRegisterRequest normalize(OwnerRegisterRequest request) {
//    	V-1
    	OwnerRegisterRequest newRequest=new OwnerRegisterRequest();
    	newRequest.setEmail(emailNormalizer.normalize(request.getEmail()));
    	newRequest.setPhone(phoneNormalizer.normalize(request.getPhone()));

        return newRequest; //----V-2
    	
//    	return new OwnerRegisterRequest(
//    			emailNormalizer.normalize(request.getEmail()),
//    			phoneNormalizer.normalize(request.getPhone())
//    			);
//    	
//    	return OwnerRegisterRequest.builder()
//    			.email(emailNormalizer.normalize(request.getEmail()))
//    			.phone(phoneNormalizer.normalize(request.getPhone()))
//    			.build();
    	
    			
    }
    
    //@TODO don't mutate parameter (create new object)
    public String normalizeEmail(String email) {
        return emailNormalizer.normalize(email);
    }

    public String normalizePhone(String phone) {
        return phoneNormalizer.normalize(phone);
    }
}