package com.url.shortner.mapper;

import com.url.shortner.entity.User;
import com.url.shortner.payload.UserUpdateRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    default void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user) {
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.mobileNo() != null) user.setPhoneNumber(request.mobileNo());
        if (request.base6dProfile() != null)
            user.setImage(Base64.getDecoder().decode(request.base6dProfile()));
    }
}