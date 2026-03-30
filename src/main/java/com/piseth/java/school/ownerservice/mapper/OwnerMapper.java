package com.piseth.java.school.ownerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Owner toOwnerDraft(OwnerRegisterRequest request);

    OwnerResponse toResponse(Owner owner);

}