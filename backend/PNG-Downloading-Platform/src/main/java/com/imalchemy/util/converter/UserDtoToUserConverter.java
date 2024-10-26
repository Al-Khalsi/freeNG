package com.imalchemy.util.converter;

import com.imalchemy.model.domain.Roles;
import com.imalchemy.model.domain.User;
import com.imalchemy.model.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, User> {

    @Override
    public User convert(UserDto source) {
        User user = new User();
        user.setId(source.getId());
        user.setUsername(source.getUsername());
        user.setEmail(source.getEmail());
        user.setLastLogin(source.getLastLogin());
        user.setLoginAttempts(source.getLoginAttempts());

        // Convert RolesDto to Roles
        source.getRoles()
                .stream().map(role -> {
                    Roles roles = new Roles();
                    roles.setId(role.getId());
                    roles.setRoleName(role.getRoleName());
                    return roles;
                })
                .toList()
                .forEach(user.getRoles()::add);
        return user;
    }

}
