package com.imalchemy.service.impl;

import com.imalchemy.config.security.jwt.JWTProvider;
import com.imalchemy.model.domain.Roles;
import com.imalchemy.model.domain.User;
import com.imalchemy.model.dto.RoleDTO;
import com.imalchemy.model.dto.UserDTO;
import com.imalchemy.model.payload.request.LoginRequest;
import com.imalchemy.repository.RolesRepository;
import com.imalchemy.repository.UserRepository;
import com.imalchemy.service.AuthenticationService;
import com.imalchemy.util.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RolesRepository roleRepository;
    private final UserConverter userConverter;
    private final JWTProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> registerUser(UserDTO userDTO) {
        User user = this.userConverter.toEntity(userDTO);

        // Encode password
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        // Handle roles
        Set<Roles> roles = new HashSet<>();
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            for (RoleDTO roleDTO : userDTO.getRoles()) {
                Roles role = this.roleRepository.findByRoleName(roleDTO.getRoleName())
                        .orElseGet(() -> {
                            Roles newRole = new Roles();
                            newRole.setRoleName(roleDTO.getRoleName());
                            return this.roleRepository.save(newRole);
                        });
                roles.add(role);
            }
        } else {
            // Add default role if no roles specified
            Roles defaultRole = this.roleRepository.findByRoleName("ROLE_USER")
                    .orElseGet(() -> {
                        Roles newRole = new Roles();
                        newRole.setRoleName("ROLE_USER");
                        return this.roleRepository.save(newRole);
                    });
            roles.add(defaultRole);
        }

        user.setRoles(roles);

        // Save the user
        User savedUser = this.userRepository.save(user);

        // Convert back to DTO
        this.userConverter.toDto(savedUser);

        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(user.getEmail(), user.getPassword());

        return Map.of(
                "userDTO", userDTO,
                "token", this.jwtProvider.createToken(authentication)
        );
    }

    @Override
    public Map<String, Object> login(LoginRequest loginRequest) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.email(), loginRequest.password());
        Authentication authenticationResponse = this.authenticationManager.authenticate(authentication);

        if (authenticationResponse == null || !authenticationResponse.isAuthenticated()) {
            throw new IllegalArgumentException("unauthorized");
        }
        String token = this.jwtProvider.createToken(authenticationResponse);

        return Map.of(
                "token", token,
                "userDetails", this.jwtProvider.extractClaims(token, this.jwtProvider.extractSecretKey())
        );
    }

}
