package com.imalchemy.config.security;

import com.imalchemy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username)
                .map(user -> {
                    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                            .map(roles -> new SimpleGrantedAuthority(roles.getRoleName()))
                            .toList();

                    return new User(user.getEmail(), user.getPassword(), authorities);
                }).orElseThrow(() -> new UsernameNotFoundException("user with the given info was not found: " + username));
    }

}
