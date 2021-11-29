package ru.itmo.blps_lab_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.blps_lab_1.data.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
