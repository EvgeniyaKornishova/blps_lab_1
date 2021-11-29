package ru.itmo.blps_lab_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.blps_lab_1.data.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByName(String name);
}
