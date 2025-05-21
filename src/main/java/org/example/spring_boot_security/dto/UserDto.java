package org.example.spring_boot_security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.spring_boot_security.model.Role;
import org.example.spring_boot_security.model.User;

import java.util.Set;
import java.util.stream.Collectors;

// DTO для ответов
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String userName;
    private String lastName;
    private int age;
    private String email;
    private String password;
    private Set<String> role;
    private User user;

    public UserDto (User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.lastName = user.getLastName();
        this.age = user.getAge();
        this.email = user.getEmail();
        this.role = user.getRole().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());
    }

}
