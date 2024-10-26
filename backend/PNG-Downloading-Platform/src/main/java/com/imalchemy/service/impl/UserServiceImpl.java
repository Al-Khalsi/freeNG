package com.imalchemy.service.impl;

import com.imalchemy.exception.NotFoundException;
import com.imalchemy.model.domain.User;
import com.imalchemy.repository.UserRepository;
import com.imalchemy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User createUser(User user) {
        return this.userRepository.save(user);
    }

}
