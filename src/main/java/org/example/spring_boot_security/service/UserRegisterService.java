package org.example.spring_boot_security.service;

import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserRegisterService {


    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public String registerUser (User user) {
        if (userService.findByUsername(user.getUsername()).isEmpty()) {
            // Добавляем роль ROLE_USER по умолчанию
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.findByRole("ROLE_USER").orElseGet(() -> roleService.save(new Role("ROLE_USER"))));
            user.setRole(roles);
            // Хеширование пароля
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            // Сохраняем пользователя
//            userService.save(user);
            return "redirect:/login?registered=true";
        } else {
            return "register";
        }
    }

}
