package com.pixelfreebies.repository;

import com.pixelfreebies.model.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByUserEmailAndUsedFalse(String email);

    Optional<PasswordResetToken> findByUserEmailAndOtpAndUsedFalse(String email, String otp);

}