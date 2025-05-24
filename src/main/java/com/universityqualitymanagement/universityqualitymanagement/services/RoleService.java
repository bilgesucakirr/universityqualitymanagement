package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.models.Role;
import com.universityqualitymanagement.universityqualitymanagement.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public String createRole(Role role) {
        roleRepository.save(role);
        return "Role created successfully";

    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

}
