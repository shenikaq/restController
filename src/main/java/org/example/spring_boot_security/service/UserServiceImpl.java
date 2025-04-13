package org.example.spring_boot_security.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;
import org.example.spring_boot_security.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Primary
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    // Получение всех пользователей с ролями
    @Override
    @Transactional
    public Optional<List<User>> findAllWithRoles() {
        try {
            return Optional.ofNullable(userRepository.findAllWithRoles())
                    .filter(list -> !list.isEmpty())
                    .map(list -> {
                        list.forEach(user ->
                                user.setRole(roleService.findRoleByUserId(user.getId()))
                        );
                        return list;
                    });
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Поиск пользователя по имени
    @Override
    @Transactional
    public Optional<User> findByUsername(String userName) {
        try {
            return Optional.of(userRepository.findByUsername(userName).get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Сохранение пользователя
    @Override
    @Transactional
    public void save(User user) {
        // Шифрование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Получение ролей из БД
        Set<Role> role = roleService.findRoleByRole(user.getRole()).get();
        // Обновление ролей пользователя
        user.setRole(role);
        // Сохранение
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        // Проверка наличия ID
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        // Поиск существующего пользователя
        User existingUser = findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));
        // Обновление полей
        updateUserFields(existingUser, user);
        // Обновление ролей
        updateUserRoles(existingUser, user.getRole());
        // Сохранение изменений
        userRepository.save(existingUser);
    }

    private void updateUserFields(User existingUser, User updatedUser) {
        if (updatedUser.getUsername() != null) {
            existingUser.setUserName(updatedUser.getUsername());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        existingUser.setAge(updatedUser.getAge());
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null &&
                !passwordEncoder.matches(updatedUser.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
    }

    private void updateUserRoles(User user, Set<Role> newRoles) {
        if (newRoles == null || newRoles.isEmpty()) {
            // Установка роли по умолчанию
            Set<String> defaults = new HashSet<>();
            defaults.add("ROLE_USER");
            Set<Role> defaultRole = roleService.findRoleByName(defaults)
                    .orElseThrow(() -> new EntityNotFoundException("Default role ROLE_USER not found"));
            newRoles = Set.of((Role) defaultRole);
        }
        // Собираем все названия ролей из новых ролей
        Set<String> roleNames = newRoles.stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
// Ищем все роли по именам
        Set<Role> managedRoles = roleService.findRoleByName(roleNames)
                .orElseThrow(() -> {
                    String missingRoles = String.join(", ", roleNames);
                    return new EntityNotFoundException("Roles not found: " + missingRoles);
                })
                .stream()
                .collect(Collectors.toSet());
        // Обновление коллекции ролей
        user.getRole().clear();
        user.getRole().addAll(managedRoles);
    }

    // Удаление пользователя по ID
    @Override
    @Transactional
    public void deleteUserById(Long id) {
        // Поиск пользователя
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        // Удаление связей
        user.getRole().clear();
        // Удаление пользователя
        userRepository.deleteUser(user);
    }

    // Поиск по ID
    @Override
    @Transactional
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Поиск по email
    @Override
    @Transactional
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}

