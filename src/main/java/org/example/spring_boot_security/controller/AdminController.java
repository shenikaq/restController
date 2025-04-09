package org.example.spring_boot_security.controller;

import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;
import org.example.spring_boot_security.service.RoleService;
import org.example.spring_boot_security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        // Получаем всех пользователей из базы данных
        List<User> users = userService.findAllWithRoles();
        // Добавляем пользователей в модель для передачи в представление
        model.addAttribute("users", users);
        model.addAttribute("url", "/admin");
        return "admin";
    }

    @GetMapping("/info")
    public String getAdmin(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + principal.getName()));
        model.addAttribute("user", user);
        model.addAttribute("url", "/admin/info");
        return "adminInfo";
    }

    // для просмотра одного пользователя
    @GetMapping("/users/info")
    public String viewUser(@RequestParam Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        model.addAttribute("user", user);
        model.addAttribute("url", "/admin/users/info");
        return "user";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }
////    /delete?id=2

    @GetMapping("/save")
    public String save(Model model, @RequestParam String userName, @RequestParam String lastName, @RequestParam Integer age, @RequestParam String email, @RequestParam String password, @RequestParam String role) {
        userService.save(userName, lastName, age, email, passwordEncoder.encode(password), role);
        model.addAttribute("url", "/admin/save");
        return "redirect:/admin";
    }
////    ?userName=gigi&password=gigi&email=gi@gi

    @GetMapping("/edit")
    public String editUser(@RequestParam Long id, @RequestParam String userName, @RequestParam String lastName, @RequestParam Integer age, @RequestParam String email, @RequestParam String password, @RequestParam (name = "role", required = false) String role) {
        userService.updateUser(id, userName, lastName, age, email, passwordEncoder.encode(password), role);
        return "redirect:/admin";
    }
////    ?id=2&userName=fn1&email=e1

}
