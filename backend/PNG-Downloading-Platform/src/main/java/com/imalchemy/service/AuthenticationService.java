package com.imalchemy.service;

import com.imalchemy.model.dto.UserDTO;

import java.util.Map;

public interface AuthenticationService {

    UserDTO registerUser(UserDTO userDTO);

    Map<String, Object> login(UserDTO userDTO);

}
