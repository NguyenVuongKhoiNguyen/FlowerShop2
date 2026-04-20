package com.poly.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.mappers.RoleMapper;
import com.poly.models.responses.RoleResponse;
import com.poly.models.services.RoleService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/dashboard/roles")
@RestController
@RequiredArgsConstructor
public class DashboardRoleController {
	
	private final RoleService rService;
	private final RoleMapper rMapper;
	
	@GetMapping
	public List<RoleResponse> getAllRole() {
		return rMapper.toResponse(rService.findAll());
	}
}
