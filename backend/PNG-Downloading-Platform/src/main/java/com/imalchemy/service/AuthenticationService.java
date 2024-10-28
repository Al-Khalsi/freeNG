package com.imalchemy.service;

import com.imalchemy.model.dto.UserDTO;
import com.imalchemy.model.payload.request.LoginRequest;

import java.util.Map;

public interface AuthenticationService {

    UserDTO registerUser(UserDTO userDTO);

    Map<String, Object> login(LoginRequest loginRequest);

}
