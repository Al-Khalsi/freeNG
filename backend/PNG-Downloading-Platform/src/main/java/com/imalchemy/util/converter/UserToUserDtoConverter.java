package com.imalchemy.util.converter;

import com.imalchemy.model.domain.User;
import com.imalchemy.model.dto.RolesDto;
import com.imalchemy.model.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {

    @Override
    public UserDto convert(User source) {
        UserDto userDto = new UserDto();
        userDto.setId(source.getId());
        userDto.setUsername(source.getUsername());
        userDto.setEmail(source.getEmail());
        userDto.setLastLogin(source.getLastLogin());
        userDto.setLoginAttempts(source.getLoginAttempts());

        // Convert Roles to RolesDto
        source.getRoles()
                .stream().map(role -> {
                    RolesDto rolesDto = new RolesDto();
                    rolesDto.setId(role.getId());
                    rolesDto.setRoleName(role.getRoleName());
                    return rolesDto;
                })
                .toList()
                .forEach(userDto.getRoles()::add);
        return userDto;
    }

}
