package ru.itmo.blpsLab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.blpsLab1.data.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
