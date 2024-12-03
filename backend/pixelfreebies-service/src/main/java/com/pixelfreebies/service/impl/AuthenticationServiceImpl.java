package com.pixelfreebies.service.impl;

import com.pixelfreebies.config.security.jwt.TokenService;
import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.model.dto.UserDTO;
import com.pixelfreebies.model.payload.request.LoginRequest;
import com.pixelfreebies.service.AuthenticationService;
import com.pixelfreebies.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenService tokenService;
    private final UserUtil userUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Map<String, Object> registerUser(UserDTO userDTO) {
        User user = this.userUtil.createAndSaveUser(userDTO);

        // Load UserDetails after saving the user
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        return this.userUtil.createAuthenticationResponse(user, authentication);
    }

    @Override
    public Map<String, Object> login(LoginRequest loginRequest) {
        Authentication authentication = this.userUtil.attemptAuthentication(loginRequest);
        String token = this.tokenService.generateToken(authentication);

        return this.userUtil.createLoginResponse(token);
    }

}
