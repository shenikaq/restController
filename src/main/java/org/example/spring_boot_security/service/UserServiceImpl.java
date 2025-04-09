package org.example.spring_boot_security.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;
import org.example.spring_boot_security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Primary
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // Получение всех пользователей с ролями
    @Override
    public List<User> findAllWithRoles() {
        return userRepository.findAllWithRoles();
    }

    // Поиск пользователя по имени
    @Override
    public Optional<User> findByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    // Сохранение пользователя
    @Override
    @Transactional
    public void save(String userName, String lastName, Integer age, String email, String password, String role) {
        userRepository.save(userName, lastName, age, email, password, role);
    }
    @Override
    @Transactional
    public void save(String userName, String lastName, Integer age, String email, String password, Set<String> role) {
        userRepository.save(userName, lastName, age, email, password, role);
    }

    // Обновление пользователя
    @Override
    @Transactional
    public void updateUser(Long id, String userName, String lastName, Integer age, String email, String password, String role) {
        userRepository.updateUser(id, userName, lastName, age, email, password, role);
    }

    // Удаление пользователя по ID
    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteUserById(id);
    }

    // Поиск по ID
    @Override
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

}

