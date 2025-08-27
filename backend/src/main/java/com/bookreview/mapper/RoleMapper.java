package com.bookreview.mapper;

import com.bookreview.model.Role;
import com.bookreview.dto.RoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDto(Role role);
    Role toEntity(RoleDto dto);
}
