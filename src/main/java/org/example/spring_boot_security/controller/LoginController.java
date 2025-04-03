package org.example.spring_boot_security.controller;

import org.example.spring_boot_security.model.User;
import org.example.spring_boot_security.model.UserRegistrationRequest;
import org.example.spring_boot_security.service.RoleService;
import org.example.spring_boot_security.service.UserRegisterService;
import org.example.spring_boot_security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRegisterService userRegisterService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserForm (@ModelAttribute User user) {
        return userRegisterService.registerUser(user);
    }

}
