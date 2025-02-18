package com.custom.service;


import com.custom.dto.request.ChangePasswordRequest;
import com.custom.dto.request.LoginRequest;
import com.custom.dto.request.RegistrationRequest;
import com.custom.dto.request.UpdateUserRequest;
import com.custom.dto.response.LoginResponse;
import com.custom.dto.response.UpdateUserResponse;
import com.custom.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    public List<com.custom.dto.response.UserResponse> getAllProfiles();
    public UserResponse registration(RegistrationRequest request);
    public UserResponse getProfileById();
    public LoginResponse login(LoginRequest request);
    public boolean deleteProfile(Long id);
    public UpdateUserResponse updateUser(UpdateUserRequest request);
    public boolean changePassword(ChangePasswordRequest newPassword);
}
