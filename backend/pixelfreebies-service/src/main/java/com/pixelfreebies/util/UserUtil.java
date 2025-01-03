package com.pixelfreebies.util;

import com.pixelfreebies.config.security.jwt.TokenService;
import com.pixelfreebies.model.domain.Roles;
import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.model.dto.UserDTO;
import com.pixelfreebies.model.payload.request.LoginRequest;
import com.pixelfreebies.repository.RolesRepository;
import com.pixelfreebies.repository.UserRepository;
import com.pixelfreebies.util.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;
    private final RolesRepository roleRepository;
    private final UserConverter userConverter;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public User createAndSaveUser(UserDTO userDTO) {
        User user = this.userConverter.toEntity(userDTO);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(this.getOrCreateDefaultRole()));

        return this.userRepository.save(user);
    }

    public Roles getOrCreateDefaultRole() {
        return this.roleRepository.findByRoleName("ROLE_USER")
                .orElseGet(() -> {
                    Roles newRole = new Roles();
                    newRole.setRoleName("ROLE_USER");
                    return this.roleRepository.save(newRole);
                });
    }

    public Map<String, Object> createAuthenticationResponse(User user, Authentication authentication) {
        UserDTO responseUserDTO = this.userConverter.toDto(user);
        responseUserDTO.setPassword(null);

        return Map.of(
                "userDTO", responseUserDTO,
                "token", this.tokenService.generateToken(authentication)
        );
    }

    public Authentication attemptAuthentication(LoginRequest loginRequest) {
        Authentication authentication = this.createAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
        );

        Authentication authenticated = this.authenticationManager.authenticate(authentication);
        if (authenticated == null || !authenticated.isAuthenticated()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return authenticated;
    }

    public Authentication createAuthenticationToken(String email, String password) {
        return UsernamePasswordAuthenticationToken.unauthenticated(email, password);
    }

    public Map<String, Object> createLoginResponse(String token) {
        Map<String, Object> claims = this.tokenService.validateTokenAndExtractClaims(token);

        return Map.of(
                "token", token,
                "userDetails", claims
        );
    }

}
