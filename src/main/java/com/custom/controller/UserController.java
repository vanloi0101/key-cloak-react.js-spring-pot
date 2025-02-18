package com.custom.controller;


import com.custom.dto.ApiResponse;
import com.custom.dto.request.ChangePasswordRequest;
import com.custom.dto.request.LoginRequest;
import com.custom.dto.request.RegistrationRequest;
import com.custom.dto.request.UpdateUserRequest;
import com.custom.dto.response.LoginResponse;
import com.custom.dto.response.UpdateUserResponse;
import com.custom.dto.response.UserResponse;
import com.custom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;


    @PostMapping("/register")
    @Operation(summary = "API đăng kí tài khoản mới")
    ApiResponse<UserResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userService.registration(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/profiles")
    @Operation(summary = "API xem tất cả người dùng trong hệ thống giành cho admin")
    ApiResponse<List<UserResponse>> getAllProfiles() {
        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .data(userService.getAllProfiles())
                .build();
    }

    @GetMapping("/profile")
    @Operation(summary = "API xem profile của người dùng")
    ApiResponse<UserResponse> getUserProfiles() {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userService.getProfileById())
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "API đăng nhập tài khoản")
    ApiResponse<LoginResponse> Login(@RequestBody LoginRequest request) {
        return ApiResponse.<LoginResponse>builder()
                .code(200)
                .data(userService.login(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "API xóa tài khoản")
    ApiResponse<Boolean> Delete(@PathVariable Long id) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .data(userService.deleteProfile(id))
                .build();
    }

    @PutMapping("/update")
    @Operation(summary = "API thay đổi thông tin người dùng")
    ApiResponse<UpdateUserResponse> Delete(@RequestBody @Valid UpdateUserRequest request) {
        return ApiResponse.<UpdateUserResponse>builder()
                .code(200)
                .data(userService.updateUser(request))
                .build();
    }

    @PutMapping("/change-password")
    @Operation(summary = "API thay đổi mật khẩu người dùng")
    ApiResponse<Boolean> Delete(@RequestBody ChangePasswordRequest password) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .data(userService.changePassword(password))
                .build();
    }
}
