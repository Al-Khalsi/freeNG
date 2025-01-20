package com.pixelfreebies.service.auth;

import com.pixelfreebies.exception.InvalidTokenException;
import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.exception.TokenExpiredException;
import com.pixelfreebies.model.dto.PasswordResetConfirmDTO;

public interface PasswordResetService {

    void initiatePasswordReset(String email) throws NotFoundException;

    void confirmPasswordReset(PasswordResetConfirmDTO resetConfirmDTO) throws InvalidTokenException, TokenExpiredException;

}
