package com.pixelfreebies.util.converter;

import com.pixelfreebies.model.domain.Roles;
import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.model.dto.RoleDTO;
import com.pixelfreebies.model.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserConverter implements Converter<User, UserDTO> {

    @Autowired
    private RoleConverter roleConverter;

    @Override
    public User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        if (dto.getRoles() != null) {
            Set<Roles> roles = dto.getRoles().stream()
                    .map(roleConverter::toEntity)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return user;
    }

    @Override
    public UserDTO toDto(User entity) {
        if (entity == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());

        if (entity.getRoles() != null) {
            Set<RoleDTO> roles = entity.getRoles().stream()
                    .map(roleConverter::toDto)
                    .collect(Collectors.toSet());
            dto.setRoles(roles);
        }

        return dto;
    }
}
