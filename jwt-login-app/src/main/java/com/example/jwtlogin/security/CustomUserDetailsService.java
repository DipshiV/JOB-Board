package com.example.jwtlogin.security;

import com.example.jwtlogin.entities.UserEntity;
import com.example.jwtlogin.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
               // .roles(user.getRole().name()) 
                .authorities("ROLE_" + user.getRole().name()) // Spring expects role names without "ROLE_" prefix here

                
                .build();
    }
}
