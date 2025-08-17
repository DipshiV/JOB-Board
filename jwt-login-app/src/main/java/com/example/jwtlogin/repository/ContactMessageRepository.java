package com.example.jwtlogin.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwtlogin.entities.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}
