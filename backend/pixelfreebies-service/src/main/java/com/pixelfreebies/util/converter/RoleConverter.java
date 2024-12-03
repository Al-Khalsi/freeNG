package com.pixelfreebies.util.converter;

import com.pixelfreebies.model.domain.Roles;
import com.pixelfreebies.model.dto.RoleDTO;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter implements Converter<Roles, RoleDTO> {

    @Override
    public Roles toEntity(RoleDTO dto) {
        if (dto == null) return null;

        Roles role = new Roles();
        role.setId(dto.getId());
        role.setRoleName(dto.getRoleName());
        return role;
    }

    @Override
    public RoleDTO toDto(Roles entity) {
        if (entity == null) return null;

        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setRoleName(entity.getRoleName());
        return dto;
    }

}
