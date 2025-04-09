package org.example.spring_boot_security.service;

import org.example.spring_boot_security.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    List<User> findAllWithRoles();
    Optional<User> findByUsername(String userName);
    void save(String userName, String lastName, Integer age, String email, String password, String role);
    void save(String userName, String lastName, Integer age, String email, String password, Set<String> role);
    void updateUser(Long id, String userName, String lastName, Integer age, String email, String password, String role);
    void deleteUserById(Long id);
    Optional<User> findById(long id);

}
