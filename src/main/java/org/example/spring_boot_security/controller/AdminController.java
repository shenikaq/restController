package org.example.spring_boot_security.controller;

import lombok.AllArgsConstructor;
import org.example.spring_boot_security.dto.UserRequest;
import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;
import org.example.spring_boot_security.dto.UserDto;
import org.example.spring_boot_security.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsersWithRoles() {
        List<UserDto> users = userService.findAllWithRoles()
                .orElse(Collections.emptyList())  // Получаем List<User>
                .stream()                         // Работаем с элементами User
                .map(this::convertToDto)          // Преобразуем User -> UserDto
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                user.getRole().stream()
                        .map(Role::getAuthority)
                        .collect(Collectors.toSet())
        );
    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> getAdminInfo(Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(convertToDto(user));
    }

    @PostMapping("/process-register")
    public ModelAndView register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
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
//    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
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
//    @PostMapping("/edit")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable long id,
            @RequestBody UserRequest request) {
        User updatedUser = userService.updateUser(id, convertToEntity(request));
        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    private User convertToEntity(UserRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setUserName(request.getUserName());
        user.setLastName(request.getLastName());
        user.setAge(request.getAge());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        return user;
    }

}
