package org.example.spring_boot_security.service;

import org.example.spring_boot_security.model.Role;

import java.util.Optional;
import java.util.Set;

public interface RoleService {

    Optional<Set<Role>> findRoleByName(Set<String> name);
    Optional<Set<Role>> findRoleByRole(Set<Role> roles);
    Set<Role> findRoleByUserId(Long userId);
    Optional<Set<Role>> save(Set<String> roleNames);

}
