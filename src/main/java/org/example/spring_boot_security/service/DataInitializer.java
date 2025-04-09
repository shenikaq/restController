package org.example.spring_boot_security.service;

import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        //// Создаем роли
        Role role1 = new Role("ROLE_USER");
        Role role2 = new Role("ROLE_ADMIN");
        //// Сохраняем роли в БД
        roleService.save(role1);
        roleService.save(role2);

//        Set<Role> roleUser = new HashSet<>();
//        roleUser.add(role1);
//
//        Set<Role> roleAdmin = new HashSet<>();
//        roleAdmin.add(role2);
//        roleAdmin.add(role1);

        Set<String> admin = new HashSet<>();
        admin.add("ROLE_ADMIN");
        admin.add("ROLE_USER");

//        Set<String> user = new HashSet<>();
//        user.add("ROLE_USER");

        userService.save("dasha", "Last", 22, "dasha@dd.ru", passwordEncoder.encode("dasha"), admin);
        userService.save("Alice", "Saf", 32, "alice@aa.ru", passwordEncoder.encode("aa"), "ROLE_USER");
        userService.save("Sara", "Bel", 12, "sara@ss.com", passwordEncoder.encode("ss"), "ROLE_USER");
        userService.save("Flora", "Kas", 42, "flora@ff.com", passwordEncoder.encode("ff"), "ROLE_USER");

    }

}
