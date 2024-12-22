package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.UserDTO;
import com.pixelfreebies.model.payload.request.LoginRequest;
import com.pixelfreebies.model.payload.response.Result;
import com.pixelfreebies.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/auth")
@Tag(name = "Authentication API", description = "Endpoints for user authentication and registration")
@SecurityRequirement(name = "BearerToken")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details"
    )
    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        Map<String, Object> responseMap = this.authenticationService.registerUser(userDTO);
        log.info("request to register url. url: {}", request.getRequestURL());

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(HttpStatus.CREATED)
                .message("User created successfully")
                .data(responseMap)
                .build()
        );
    }

    @Operation(summary = "User Login", description = "Authenticate user and generate access token")
    @PostMapping("/login")
    public ResponseEntity<Result> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, Object> result = this.authenticationService.login(loginRequest);
        log.info("request to login url. url: {}", request.getRequestURL());

        return ResponseEntity.status(OK)
                .header(HttpHeaders.AUTHORIZATION, String.valueOf(result.get("token")))
                .body(Result.builder()
                        .flag(true)
                        .code(HttpStatus.OK)
                        .message("User logged-in successfully")
                        .data(result)
                        .build()
                );
    }

}
