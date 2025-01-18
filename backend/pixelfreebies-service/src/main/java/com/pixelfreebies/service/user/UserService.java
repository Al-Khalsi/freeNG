package com.pixelfreebies.service.user;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.User;

public interface UserService {

    User findByEmail(String email) throws NotFoundException;

}
