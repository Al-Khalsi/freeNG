package com.imalchemy.controller;

import com.imalchemy.model.dto.UserDTO;
import com.imalchemy.model.payload.response.Result;
import com.imalchemy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Result> createUser(@RequestBody UserDTO userDTO) {
        UserDTO result = this.userService.createUser(userDTO);

        return ResponseEntity.ok(Result.builder()
                .flag(true)
                .code(HttpStatus.CREATED)
                .message("User created successfully")
                .data(result)
                .build()
        );
    }

}
