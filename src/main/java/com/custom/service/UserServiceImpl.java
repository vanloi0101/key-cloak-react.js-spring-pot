package com.custom.service;

import com.custom.dto.keycloak.LoginRequestParam;
import com.custom.dto.request.ChangePasswordRequest;
import com.custom.dto.request.LoginRequest;
import com.custom.dto.request.RegistrationRequest;
import com.custom.dto.request.UpdateUserRequest;
import com.custom.dto.response.LoginResponse;
import com.custom.dto.response.UpdateUserResponse;
import com.custom.dto.response.UserResponse;
import com.custom.entity.User;
import com.custom.exception.AppException;
import com.custom.exception.ErrorCode;
import com.custom.exception.ErrorNormalizer;
import com.custom.mapper.UserMapper;
import com.custom.repository.KeyCloakRepository;
import com.custom.repository.UserRepository;
import com.custom.utils.AuthenUtil;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    KeyCloakRepository keyCloakRepository;
    ErrorNormalizer errorNormalizer;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    // Đọc các giá trị cấu hình từ file application.properties

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;


    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;



    @Override
    public List<UserResponse> getAllProfiles() {
        var profiles = userRepository.findAll();
        return profiles.stream().map(userMapper::toUserReponse).toList();
    }

    @Override
    public UserResponse registration(RegistrationRequest request) {
        try {
            User user = userMapper.toUser(request);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return userMapper.toUserReponse(user);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public UserResponse getProfileById() {
        Long userId = AuthenUtil.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserReponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            var token = keyCloakRepository.exchangeToken(LoginRequestParam.builder()
                    .grant_type("password")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .scope("openid")
                    .build());

            return LoginResponse.builder()
                    .accessToken(token.getAccessToken())
                    .refreshToken(token.getRefreshToken())
                    .build();
        } catch (FeignException e) {
            throw new AppException(ErrorCode.INVALID_USERNAME_PASSWORD);
        }
    }

    @Override
    public boolean deleteProfile(Long id) {
        try {
            User theUser = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.EMAIL_EXISTED));
            userRepository.delete(theUser);
            return true;
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public UpdateUserResponse updateUser(UpdateUserRequest request) {
        try {
            Long userId = AuthenUtil.getUserId();
            var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            userRepository.save(user);

            return userMapper.toUpdateUserResponse(user);
        }catch (FeignException exception){
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public boolean changePassword(ChangePasswordRequest newPassword) {
        Long userId = AuthenUtil.getUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setPassword(bCryptPasswordEncoder.encode(newPassword.getNewPassword()));
        var response = userRepository.save(user);

        if (response == null) {
            return false;
        }
        return true;
    }

    private String extractUserId(ResponseEntity<?> response){
        String location = response.getHeaders().get("Location").get(0);
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }
}
