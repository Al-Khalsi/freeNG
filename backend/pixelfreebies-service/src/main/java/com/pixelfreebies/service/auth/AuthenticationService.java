package com.pixelfreebies.service.auth;

import com.pixelfreebies.model.dto.UserDTO;
import com.pixelfreebies.model.payload.request.LoginRequest;

import java.util.Map;

public interface AuthenticationService {

    Map<String, Object> registerUser(UserDTO userDTO);

    Map<String, Object> login(LoginRequest loginRequest);

}
