package com.pixelfreebies.service.auth;

import com.pixelfreebies.exception.InvalidTokenException;
import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.exception.TokenExpiredException;
import com.pixelfreebies.model.domain.PasswordResetToken;
import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.model.dto.PasswordResetConfirmDTO;
import com.pixelfreebies.repository.PasswordResetTokenRepository;
import com.pixelfreebies.repository.UserRepository;
import com.pixelfreebies.service.email.EmailSenderService;
import com.pixelfreebies.service.user.UserService;
import com.pixelfreebies.util.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserService userService;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private @Value("${app.password-reset.otp.length:6}") int otpCodeLength;
    private @Value("${app.password-reset.otp.expiry-minutes:10}") int otpCodeExpirationTimeInMinutes;

    @Override
    @Transactional
    public void initiatePasswordReset(String email) throws NotFoundException {
        User user = this.userService.findByEmail(email);

        // Invalidate any existing OTP
        this.passwordResetTokenRepository.findByUserEmailAndUsedFalse(email)
                .ifPresent(token -> {
                    token.setUsed(true);
                    this.passwordResetTokenRepository.save(token);
                });

        // Generate a new 6-digit random alphanumeric OTP
        String otp = RandomCodeGenerator.generateRandomCode(otpCodeLength);
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, otp, this.otpCodeExpirationTimeInMinutes);
        this.passwordResetTokenRepository.save(passwordResetToken);

        // Send OTP via email
        this.emailSenderService.sendEmail(email, "Reset Your Password", this.prepareBody(otp));
    }

    private String prepareBody(String otp) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Reset Your Password</title>
                        <style>
                            body {
                                margin: 0;
                                padding: 0;
                                font-family: 'Arial', sans-serif;
                                background-color: #12102b;
                                color: #ffffff;
                            }
                            .email-container {
                                max-width: 600px;
                                margin: 20px auto;
                                background-color: #1c1b3a;
                                border-radius: 10px;
                                overflow: hidden;
                                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                            }
                            .header {
                                padding: 20px;
                                text-align: center;
                                background-color: #362465;
                                color: #ffffff;
                            }
                            .header h1 {
                                margin: 0;
                                font-size: 24px;
                            }
                            .header h1 span {
                                font-size: 36px;
                                color: #6f42c1;
                            }
                            .content {
                                padding: 20px;
                                line-height: 1.6;
                            }
                            .otp {
                                display: block;
                                margin: 20px 0;
                                text-align: center;
                                font-size: 24px;
                                color: #6f42c1;
                                font-weight: bold;
                            }
                            .footer {
                                padding: 10px;
                                text-align: center;
                                background-color: #362465;
                                color: #bbbbbb;
                                font-size: 14px;
                            }
                            .footer a {
                                color: #6f42c1;
                                text-decoration: none;
                            }
                            .footer a:hover {
                                text-decoration: underline;
                            }
                            .button {
                                display: inline-block;
                                padding: 10px 20px;
                                background-color: #6f42c1;
                                color: #ffffff;
                                text-decoration: none;
                                border-radius: 5px;
                                margin: 10px auto;
                                text-align: center;
                                font-size: 16px;
                            }
                            .button:hover {
                                background-color: #54328c;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="email-container">
                            <div class="header">
                                <h1><span>P</span>ixelFreebies</h1>
                                <p>Your first go-to image website</p>
                            </div>
                            <div class="content">
                                <p>Hi [User],</p>
                                <p>You requested to reset your password. Use the one-time OTP below to reset it. Please note that this OTP is valid only for the next 10 minutes.</p>
                                <div class="otp">[OTP]</div>
                                <p>If you did not request this password reset, please ignore this email or <a href="#">contact support</a> if you have concerns.</p>
                                <p>Click the button below to reset your password:</p>
                                <a href="https://pixelfreebies.com/reset-password" class="button">Reset Password</a>
                                <p>Thank you,<br>PixelFreebies Team</p>
                            </div>
                            <div class="footer">
                                <p>&copy; 2025 PixelFreebies. All Rights Reserved.</p>
                                <p><a href="https://pixelfreebies.com/privacy">Privacy Policy</a> | <a href="https://pixelfreebies.com/terms">Terms of Service</a></p>
                            </div>
                        </div>
                    </body>
                </html>
                """.replace("[OTP]", otp);
    }

    @Override
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmDTO resetConfirmDTO) throws InvalidTokenException, TokenExpiredException {
        PasswordResetToken token = this.passwordResetTokenRepository
                .findByUserEmailAndOtpAndUsedFalse(resetConfirmDTO.getEmail(), resetConfirmDTO.getOtp())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired OTP"));

        if (token.isExpired()) {
            throw new TokenExpiredException("OTP has expired");
        }

        User user = token.getUser();
        user.setPassword(this.passwordEncoder.encode(resetConfirmDTO.getNewPassword()));
        this.userRepository.save(user);

        token.setUsed(true);
        this.passwordResetTokenRepository.save(token);
    }

}
