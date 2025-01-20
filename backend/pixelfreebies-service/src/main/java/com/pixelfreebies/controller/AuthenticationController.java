package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.PasswordResetConfirmDTO;
import com.pixelfreebies.model.dto.UserDTO;
import com.pixelfreebies.model.payload.request.LoginRequest;
import com.pixelfreebies.model.payload.response.Result;
import com.pixelfreebies.service.auth.AuthenticationService;
import com.pixelfreebies.service.auth.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final PasswordResetService passwordResetService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details"
    )
    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody UserDTO userDTO) {
        Map<String, Object> responseMap = this.authenticationService.registerUser(userDTO);

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
    public ResponseEntity<Result> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = this.authenticationService.login(loginRequest);

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

    @PostMapping("/forget-password/reset-request")
    @Operation(summary = "Forget Password - Request Reset", description = "Send forget password request.")
    public ResponseEntity<Result> requestPasswordReset(@Valid @RequestParam String email) {
        this.passwordResetService.initiatePasswordReset(email);
        return ResponseEntity.ok(Result.success("Password reset OTP sent successfully"));
    }

    @PostMapping("/forget-password/reset-confirm")
    @Operation(summary = "Forget Password - Confirm Reset", description = "Reset password.")
    public ResponseEntity<Result> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmDTO confirmDTO) {
        this.passwordResetService.confirmPasswordReset(confirmDTO);
        return ResponseEntity.ok(Result.success("Password reset successfully"));
    }

}
