package com.pixelfreebies.service.user;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByEmail(String email) throws NotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

}
