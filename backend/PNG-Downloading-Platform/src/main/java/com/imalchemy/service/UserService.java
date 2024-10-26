package com.imalchemy.service;

import com.imalchemy.model.domain.User;

public interface UserService {

    User getUser(String email);

    User createUser(User user);

}
