package org.example.spring_boot_security.controller;

import org.example.spring_boot_security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/process-register")
    public String register(Model model, @RequestParam String userName, @RequestParam String lastName, @RequestParam Integer age, @RequestParam String email, @RequestParam String password, @RequestParam String role) {
        userService.save(userName, lastName, age, email, passwordEncoder.encode(password), role);
        model.addAttribute("url", "/register");
        return "redirect:/admin";
    }

    @GetMapping("/register")
    public String registerUserForm (Model model) {
        model.addAttribute("url", "/register");
        return "register";
    }

}
