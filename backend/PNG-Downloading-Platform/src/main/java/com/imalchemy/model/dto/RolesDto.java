package com.imalchemy.model.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.imalchemy.model.domain.Roles}
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolesDto implements Serializable {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String roleName;

    private Set<UserDto> users;

}