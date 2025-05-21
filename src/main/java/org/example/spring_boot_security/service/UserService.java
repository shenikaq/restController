package org.example.spring_boot_security.service;

import org.example.spring_boot_security.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<List<User>> findAllWithRoles();
    Optional<User> findByUsername(String userName);
    User save(User user);
    User updateUser(Long id, User user);
    void deleteUserById(Long id);

}
