package org.example.spring_boot_security.repository;

import org.example.spring_boot_security.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAllWithRoles();
    Optional<User> findByUsername(String username);
    void save(User user);
    void deleteUser(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

}
