package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poly.models.entities.Role;
import com.poly.models.requests.RoleRequest;
import com.poly.models.responses.RoleResponse;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "accountRoles", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    Role toEntity(RoleRequest request);

    List<RoleResponse> toResponse(List<Role> roles);
}
