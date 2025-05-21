package org.example.spring_boot_security.controller;

import lombok.AllArgsConstructor;
import org.example.spring_boot_security.dto.UserRequest;
import org.example.spring_boot_security.model.User;
import org.example.spring_boot_security.dto.UserDto;
import org.example.spring_boot_security.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

@AllArgsConstructor
@RestController
@RequestMapping("/admin/api")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsersWithRoles() {
        List<UserDto> users = userService.findAllWithRoles()
                .orElse(Collections.emptyList())  // Получаем List<User>
                .stream()                         // Преобразуем поток User в поток UserDto
                .map(UserDto::new)                // Используем конструктор UserDto(User user)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> getAdminInfo(Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(new UserDto(user));
    }

    @PostMapping("/process-register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRequest userRequest) {
        // Проверяем и инициализируем roles, если они не предоставлены
        if (userRequest.getRoles() == null) {
            userRequest.setRoles(new HashSet<>()); // или установите роль по умолчанию
        }
        User user = new User(userRequest);
        User savedUser = userService.save(user);
        // Используем конструктор UserDto(User user)
        UserDto userDto = new UserDto(savedUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/users/" + savedUser.getId())
                .body(userDto);
    }

    @GetMapping("/register")
    public ResponseEntity<Map<String, String>> getRegistrationInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration endpoint");
        response.put("registration_url", "/process-register");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // Сохранение пользователя
    @PostMapping("/save")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        User user = new User(userRequest);
        User savedUser = userService.save(user);
        UserDto response = new UserDto(savedUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/users/" + savedUser.getId())
                .body(response);
    }

    // Обновление пользователя
    @PutMapping("/edit/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable long id,
            @RequestBody UserRequest request) {
        // Проверяем соответствие ID в пути и теле запроса
        if (id != (request.getId())) {
            throw new IllegalArgumentException("User ID in path and body must match");
        }
        User updatedUser = userService.updateUser(id, new User(request));
        return ResponseEntity.ok(new UserDto(updatedUser));
    }

}
