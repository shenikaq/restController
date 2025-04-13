package org.example.spring_boot_security.service;

import lombok.AllArgsConstructor;
import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Optional<Set<Role>> findRoleByName(Set<String> roleName) {
        // Нормализация входных данных
        Set<String> normalizedNames = normalizeNames(roleName);
        return roleRepository.findRoleByName(normalizedNames);
    }

    @Override
    @Transactional
    public Optional<Set<Role>> findRoleByRole(Set<Role> roles) {
        return roleRepository.findRoleByRole(roles);
    }

    @Override
    @Transactional
    public Set<Role> findRoleByUserId(Long userId) {
        return roleRepository.findRoleByUserId(userId);
    }

    @Override
    @Transactional
    public Optional<Set<Role>> save(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Optional.empty();
        }
        // нормализация имен
        Set<String> normalizedNames = normalizeNames(roleNames);
        // проверка существующих ролей
        Set<Role> existingRoles = findRoleByName(normalizedNames)
                .orElse(Collections.emptySet());
        // фильтрация новых ролей
        Set<Role> newRoles = createNewRoles(normalizedNames, existingRoles);
        if (!newRoles.isEmpty()) {
            roleRepository.save(newRoles);
        }
        Set<Role> allRoles = new HashSet<>(existingRoles);
        allRoles.addAll(findRoleByName(normalizedNames)
                .orElse(Collections.emptySet()));
        return allRoles.isEmpty()
                ? Optional.empty()
                : Optional.of(allRoles);
    }

    private Set<String> normalizeNames(Set<String> names) {
        return names.stream()
                .map(name -> name.trim().toUpperCase())
                .collect(Collectors.toSet());
    }

    private Set<Role> createNewRoles(Set<String> names, Set<Role> existing) {
        Set<String> existingNames = existing.stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
        return names.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> {
                    Role role = new Role();
                    role.setRole(name);
                    return role;
                })
                .collect(Collectors.toSet());
    }

}
