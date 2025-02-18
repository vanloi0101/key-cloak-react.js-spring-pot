package com.custom.repository;

import com.custom.dto.keycloak.*;
import com.custom.dto.response.LoginResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms", url = "${idp.url}")
public interface KeyCloakRepository {

    @PostMapping(value = "/realms/${idp.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap LoginRequestParam param);

    @PostMapping(value = "/realms/${idp.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LoginResponse login(@QueryMap LoginRequestParam param);

    @PostMapping(value = "/admin/realms/${idp.realm}/users",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserCreationParam param);

    @DeleteMapping(value = "/admin/realms/${idp.realm}/users/{userId}")
    ResponseEntity<?> deleteUser(
            @RequestHeader("Authorization") String token,
            @PathVariable("userId") String userId);

    @PutMapping(value = "/admin/realms/${idp.realm}/users/{userId}")
    ResponseEntity<?> updateUser(
            @RequestHeader("Authorization") String token,
            @PathVariable("userId") String userId,
            @RequestBody UpdateRequestParam request);

    @PutMapping(value = "/admin/realms/${idp.realm}/users/{userId}/reset-password")
    ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @PathVariable("userId") String userId,
            @RequestBody Credential request);
}
