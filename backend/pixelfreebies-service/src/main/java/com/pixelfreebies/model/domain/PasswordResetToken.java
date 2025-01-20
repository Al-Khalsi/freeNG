package com.pixelfreebies.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String otp;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    private boolean used = false;

    // -------------------- Relationships --------------------
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // -------------------- Methods --------------------
    public PasswordResetToken(User user, String otp, int expiryTimeInMinutes) {
        this.user = user;
        this.otp = otp;
        this.expiresAt = LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
        this.used = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

}

