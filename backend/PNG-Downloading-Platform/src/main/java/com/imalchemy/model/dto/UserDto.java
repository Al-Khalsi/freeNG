package com.imalchemy.model.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link com.imalchemy.model.domain.User}
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String username;
    private String email;
    private LocalDateTime lastLogin;
    private int loginAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<RolesDto> roles;
}