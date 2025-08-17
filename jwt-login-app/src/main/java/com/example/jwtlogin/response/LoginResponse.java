package com.example.jwtlogin.response;

import com.example.jwtlogin.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private Role role;
    private String email;
    private String name;
}
