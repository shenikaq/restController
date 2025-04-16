package org.example.spring_boot_security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.spring_boot_security.model.Role;

import java.util.Set;

// DTO для запросов
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UserRequest {

    private Long id;
    private String userName;
    private String lastName;
    private int age;
    private String email;
    private String password;
    private Set<Role> role;
}
