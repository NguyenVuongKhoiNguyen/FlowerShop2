package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poly.models.entities.Role;
import com.poly.models.requests.RoleRequest;
import com.poly.models.responses.RoleResponse;
import com.poly.models.services.RoleService;

@Component
@Mapper(componentModel = "spring")
public abstract class RoleMapper {
	
	@Autowired
	protected RoleService roleService;
	
	@Mapping(target = "accountRoles", ignore = true) //always ignore one to many
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "fullname", ignore = true)
	abstract Role toEntity(RoleRequest request);
	
	abstract RoleResponse toResponse(Role role);
	
	abstract List<RoleResponse> toResponseList(List<Role> role);
	
	@AfterMapping
	protected void fillRoleEmptyFields(RoleRequest request, @MappingTarget Role role) { //user after mapping for extra logic
		if (request.getId() == null) return;
		Role exist = roleService.findById(request.getId());
		role.setFullname(exist.getFullname());
		role.setName(exist.getName());
	}
}
