package com.imalchemy.controller;

import com.imalchemy.model.dto.UserDTO;
import com.imalchemy.model.payload.request.LoginRequest;
import com.imalchemy.model.payload.response.Result;
import com.imalchemy.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody UserDTO userDTO) {
        UserDTO result = this.authenticationService.registerUser(userDTO);

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(HttpStatus.CREATED)
                .message("User created successfully")
                .data(result)
                .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Result> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = this.authenticationService.login(loginRequest);

        return ResponseEntity.status(OK)
                .header(HttpHeaders.AUTHORIZATION, String.valueOf(result.get("token")))
                .body(Result.builder()
                        .flag(true)
                        .code(HttpStatus.CREATED)
                        .message("User logged-in successfully")
                        .data(result)
                        .build()
                );
    }

}
