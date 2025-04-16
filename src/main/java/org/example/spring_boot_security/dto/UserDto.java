package org.example.spring_boot_security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

// DTO для ответов
//@Bean
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

    public UserDto(Long id, String userName, String lastName, int age, String email, Set<String> role) {
        this.id = id;
        this.userName = userName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.role = role;
    }
}
