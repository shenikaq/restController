package org.example.spring_boot_security.repository;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.example.spring_boot_security.model.Role;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Set<Role>> findRoleByName(Set<String> roleName) {
        List<Role> roles = entityManager.createQuery(
                        "SELECT r FROM Role r WHERE r.role IN :roleNames",
                        Role.class)
                .setParameter("roleNames", roleName)
                .getResultList();
        return Optional.ofNullable(roles.isEmpty() ? null : new HashSet<>(roles));
    }

    @Override
    public Set<Role> findRoleByUserId(Long userId) {
        return new HashSet<>(entityManager.createQuery("""
            SELECT r
            FROM User u
            JOIN u.role r
            WHERE u.id = :userId""", Role.class)
                .setParameter("userId", userId)
                .getResultStream()
                .collect(Collectors.toSet()));
    }

    @Override
    public void save(Set<Role> newRoles) {
        newRoles.forEach(entityManager::persist);
        entityManager.flush();
    }
}
