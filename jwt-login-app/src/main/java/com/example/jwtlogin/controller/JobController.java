package com.example.jwtlogin.controller;

import com.example.jwtlogin.entities.Application;
import com.example.jwtlogin.entities.Job;
import com.example.jwtlogin.entities.UserEntity;
import com.example.jwtlogin.repository.JobRepository;
import com.example.jwtlogin.repository.UserRepository;
import com.example.jwtlogin.request.ApplicantDTO;
import com.example.jwtlogin.request.JobDTO;

import com.example.jwtlogin.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addJob(@RequestBody JobDTO jobDTO, @RequestParam Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    Job job = new Job();
                    job.setTitle(jobDTO.getTitle());
                    job.setDescription(jobDTO.getDescription());
                    job.setCompany(jobDTO.getCompany());
                    job.setLocation(jobDTO.getLocation());
                    job.setSalary(jobDTO.getSalary());
                    job.setPostedDate(LocalDate.now());
                    job.setCreatedBy(user);
                    jobRepository.save(job);
                    return ResponseEntity.ok("Job created successfully");
                })
                .orElse(ResponseEntity.badRequest().body("User not found"));
    }

    @GetMapping("/created-by/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Job>> getJobsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(jobRepository.findByCreatedById(userId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam String keyword) {
        return ResponseEntity.ok(jobRepository.findByTitleContainingIgnoreCase(keyword));
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateJob(@PathVariable Long jobId, @RequestBody JobDTO jobDTO) {
        return jobRepository.findById(jobId)
                .map(job -> {
                    job.setTitle(jobDTO.getTitle());
                    job.setDescription(jobDTO.getDescription());
                    job.setCompany(jobDTO.getCompany());
                    job.setLocation(jobDTO.getLocation());
                    job.setSalary(jobDTO.getSalary());
                    job.setPostedDate(jobDTO.getPostedDate() != null ? jobDTO.getPostedDate() : job.getPostedDate());
                    jobRepository.save(job);
                    return ResponseEntity.ok("Job updated successfully");
                })
                .orElse(ResponseEntity.badRequest().body("Job not found"));
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            return ResponseEntity.badRequest().body("Job not found");
        }
        jobRepository.deleteById(jobId);
        return ResponseEntity.ok("Job deleted successfully");
    }

    @GetMapping("/applicants/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ApplicantDTO>> getApplicantsByJob(@PathVariable Long jobId) {
        List<ApplicantDTO> applicants = applicationRepository.findByJobId(jobId).stream()
                .map(app -> new ApplicantDTO(
                        app.getId(),
                        app.getUser().getName(),
                        app.getUser().getEmail(),
                        null,
                        app.getStatus()
                )).collect(Collectors.toList());
        System.out.println("Reached applicants API for jobId = " + jobId);

        return ResponseEntity.ok(applicants);
    }

    @GetMapping("/user-id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Long> getUserId(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(user.getId()))
                .orElse(ResponseEntity.notFound().build());
    }
}
