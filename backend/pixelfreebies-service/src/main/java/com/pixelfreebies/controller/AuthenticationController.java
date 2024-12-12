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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "flag": true,
                                      "code": "CREATED",
                                      "message": "User created successfully",
                                      "data": {
                                        "token": "eyJhbGciOiJIUzI1NiJ9...",
                                        "userDTO": {
                                          "id": "9962c489-70e1-4043-b8a5-7044a344321b",
                                          "username": "string",
                                          "email": "string",
                                          "password": null,
                                          "roles": [
                                            {
                                              "id": 1,
                                              "roleName": "ROLE_USER"
                                            }
                                          ]
                                        }
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "flag":false,
                                        "code":"INTERNAL_SERVER_ERROR",
                                        "message":"internal server error occurred.",
                                        "data":"some message from exception.getMessage()"
                                    }
                                    """)
                    )
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful Login",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "flag":true,
                                        "code":"CREATED",
                                        "message":"User logged-in successfully",
                                        "data": {
                                            "token":"eyJhbGciOiJIUzI1NiJ9...",
                                            "userDetails": {
                                                "email":"string",
                                                "username":"string",
                                                "role":"ROLE_USER"
                                            }
                                        }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid Credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                            "flag":false,
                                            "code":"UNAUTHORIZED",
                                            "message":"username or password is incorrect.",
                                            "data":"User not found: stringg"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "flag":false,
                                        "code":"INTERNAL_SERVER_ERROR",
                                        "message":"internal server error occurred.",
                                        "data":"some message from exception.getMessage()"
                                    }
                                    """)
                    )
            )
    })
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
