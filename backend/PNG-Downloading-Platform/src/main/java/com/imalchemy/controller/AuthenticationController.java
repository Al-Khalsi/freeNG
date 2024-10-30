package com.imalchemy.controller;

import com.imalchemy.model.dto.UserDTO;
import com.imalchemy.model.payload.request.LoginRequest;
import com.imalchemy.model.payload.response.Result;
import com.imalchemy.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication API", description = "Endpoints for user authentication and registration")
@SecurityRequirement(name = "BearerToken")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Result.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
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

    @Operation(
            summary = "User login",
            description = "Authenticates user and returns JWT token + add the token in the header"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged in",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Result.class)
                    ),
                    headers = @Header(
                            name = HttpHeaders.AUTHORIZATION,
                            description = "JWT token for authentication"
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            )
    })
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
