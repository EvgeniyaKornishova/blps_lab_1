package ru.itmo.blpsLab1.service;

import org.springframework.stereotype.Service;
import ru.itmo.blpsLab1.data.Role;
import ru.itmo.blpsLab1.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
