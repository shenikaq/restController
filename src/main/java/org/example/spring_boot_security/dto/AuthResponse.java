package org.example.spring_boot_security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String userName;
    private Set<String> roles;

}
