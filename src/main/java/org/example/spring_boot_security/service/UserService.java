package org.example.spring_boot_security.service;

import org.example.spring_boot_security.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<List<User>> findAllWithRoles();
    Optional<User> findByUsername(String userName);
    void save(User user);
    void updateUser(User user);
    void deleteUserById(Long id);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

}
