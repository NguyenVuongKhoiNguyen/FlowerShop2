package com.poly.models.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.poly.models.entities.Role;
import com.poly.models.repositories.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepo;              
    
    public Role getReferenceById(Integer id) {
        return roleRepo.getReferenceById(id);
    }
    
    public List<Role> findAll() {
        return roleRepo.findAll();
    }

    public Role findByName(String name) {
        return roleRepo.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + name));
    }
}
