package org.example.spring_boot_security.util;

import lombok.AllArgsConstructor;
import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;
import org.example.spring_boot_security.service.RoleService;
import org.example.spring_boot_security.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Override
    public void run(String... args) {

        Set<String> roleAdmin = new HashSet<>();
        roleAdmin.add("ROLE_USER");
        roleAdmin.add("ROLE_ADMIN");
        //// Сохраняем роли в БД
        roleService.save(roleAdmin);

        Set<String> roleUser = new HashSet<>();
        roleUser.add("ROLE_USER");

        Set<Role> user = roleService.findRoleByName(roleUser).get();
        Set<Role> admin = roleService.findRoleByName(roleAdmin).get();

        User user1 = new User("dasha", "Last", 22, "dasha@dd.ru", "dd", admin);
        userService.save(user1);
        User user2 = new User("Alice", "Saf", 32, "alice@aa.ru", "aa", user);
        userService.save(user2);
        User user3 = new User("Sara", "Bel", 12, "sara@ss.com", "ss", user);
        userService.save(user3);
        User user4 = new User("Flora", "Kas", 42, "flora@ff.com", "ff", user);
        userService.save(user4);

    }

}
