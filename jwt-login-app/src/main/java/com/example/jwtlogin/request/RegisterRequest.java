package com.example.jwtlogin.request;

import com.example.jwtlogin.entities.*;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;  // USER or ADMIN
}
