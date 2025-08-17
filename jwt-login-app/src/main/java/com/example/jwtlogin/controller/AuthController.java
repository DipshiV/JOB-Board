package com.example.jwtlogin.controller;

import com.example.jwtlogin.entities.Role;
import com.example.jwtlogin.entities.UserEntity;
import com.example.jwtlogin.repository.UserRepository;
import com.example.jwtlogin.request.LoginRequest;
import com.example.jwtlogin.request.RegisterRequest;
import com.example.jwtlogin.response.LoginResponse;
import com.example.jwtlogin.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }

        UserEntity user = new UserEntity();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getEmail(),
//                            loginRequest.getPassword()
//                    )
//            );
//
//            String token = tokenProvider.generateToken(loginRequest.getEmail());
//
//            return ResponseEntity.ok(token);
//
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(401).body("Invalid email or password");
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserEntity user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            String token = tokenProvider.generateToken(user);


            LoginResponse response = new LoginResponse(token, "Bearer", user.getRole(),user.getEmail(),user.getName());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    

    


//    @GetMapping("/test")
//    public ResponseEntity<?> testSecured() {
//        return ResponseEntity.ok("JWT Token is valid! Access granted.");
//    }
}
