package org.example.spring_boot_security.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.AllArgsConstructor;
import org.example.spring_boot_security.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final EntityManager entityManager;

    @Override
    public List<User> findAllWithRoles() {
        return entityManager.createQuery("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.role""", User.class)
                .getResultList();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.userName = :username",
                            User.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(User user) {
        entityManager.merge(user); // Обновляем связь ManyToMany
    }

    @Override
    public void deleteUser(User user) {
        entityManager.remove(user);
    }

    @Override
    public Optional<User> findById(Long userId) {
        try {
            return Optional.of(entityManager.createQuery("""
                SELECT DISTINCT u
                FROM User u
                LEFT JOIN FETCH u.role
                WHERE u.id = :userId""", User.class)
                    .setParameter("userId", userId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            return Optional.of(entityManager.createQuery("""
                SELECT DISTINCT u
                FROM User u
                LEFT JOIN FETCH u.role
                WHERE u.email = :email""", User.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}
