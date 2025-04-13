package org.example.spring_boot_security.controller;

import lombok.AllArgsConstructor;
import org.example.spring_boot_security.model.User;
import org.example.spring_boot_security.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@AllArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @GetMapping
    public ModelAndView listUsers() {
        ModelAndView mav = new ModelAndView("admin");
        mav.addObject("users", userService.findAllWithRoles());
        mav.addObject("url", "/admin");
        return mav;
    }

    @GetMapping("/info")
    public ModelAndView getAdmin(Principal principal) {
        ModelAndView mav = new ModelAndView("adminInfo");
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));
        mav.addObject("user", user);
        mav.addObject("url", "/admin/info");
        return mav;
    }

    @PostMapping("/process-register")
    public ModelAndView register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Сохранение пользователя
        userService.save(user);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin");
        modelAndView.addObject("url", "/register");
        redirectAttributes.addFlashAttribute("message", "User registered successfully!");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView registerUserForm() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("url", "/register");
        return modelAndView;
    }

    @GetMapping("/delete")
    public ModelAndView delete(@RequestParam long id) {
        userService.deleteUserById(id);
        return new ModelAndView("redirect:/admin");
    }

    // Сохранение пользователя
    @PostMapping("/save")
    public ModelAndView save(@ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("admin");
        }
        userService.save(user);
        return new ModelAndView("redirect:/admin");
    }

    // Обновление пользователя
    @PostMapping("/edit")
    public ModelAndView updateUser(@ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("admin");
        }
        userService.updateUser(user);
        return new ModelAndView("redirect:/admin");
    }

}
