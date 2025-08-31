package com.bookreview.mapper;

import com.bookreview.model.User;
import com.bookreview.model.Role;
import com.bookreview.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "userId", target = "username")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserDto toDto(User user);

    // DTO-to-entity mapping for roles is handled in the service layer if needed

    @Named("rolesToStrings")
    public static Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }

    @Named("stringsToRoles")
    public static Set<Role> stringsToRoles(Set<String> names) {
        // This should be implemented to fetch Role entities by name from DB in service layer
        return null;
    }
}
