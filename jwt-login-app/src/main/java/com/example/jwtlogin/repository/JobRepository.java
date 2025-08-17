package com.example.jwtlogin.repository;

import com.example.jwtlogin.entities.Job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
   
	    List<Job> findByCreatedById(Long userId); 
	List<Job> findByTitleContainingIgnoreCase(String keyword);
}
