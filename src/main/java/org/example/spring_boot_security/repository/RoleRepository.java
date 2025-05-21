package org.example.spring_boot_security.repository;

import org.example.spring_boot_security.model.Role;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository {

    Optional<Set<Role>> findRoleByName(Set<String> roleName);
    Set<Role> findRoleByUserId(Long userId);
    void save(Set<Role> newRoles);

}
