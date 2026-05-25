package com.poly.models.services.impl;

import org.springframework.stereotype.Service;

import com.poly.models.entities.Role;
import com.poly.models.repositories.RoleRepository;
import com.poly.models.services.RoleService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepo;

	@Override
	public Role findById(Integer id) {
		// TODO Auto-generated method stub
		return roleRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));
	}              
    
}
