package com.custom.mapper;

import com.custom.dto.request.RegistrationRequest;
import com.custom.dto.request.UpdateUserRequest;
import com.custom.dto.response.UpdateUserResponse;
import com.custom.dto.response.UserResponse;
import com.custom.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegistrationRequest request);
    UserResponse toUserReponse(User user);
    User toUpdateProfile(UpdateUserRequest request);
    UpdateUserResponse toUpdateUserResponse(User user);
}
