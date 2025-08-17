package com.example.jwtlogin.controller;

import com.example.jwtlogin.entities.Application;
import com.example.jwtlogin.entities.Job;
import com.example.jwtlogin.entities.UserEntity;
import com.example.jwtlogin.repository.ApplicationRepository;
import com.example.jwtlogin.repository.JobRepository;
import com.example.jwtlogin.repository.UserRepository;
import com.example.jwtlogin.request.ApplicationResponseDTO;
import com.example.jwtlogin.request.MyApplicationDTO;
import com.example.jwtlogin.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> applyJob(
            @RequestParam("jobId") Long jobId,
            @RequestParam("resume") MultipartFile resumeFile,
            @RequestParam(value = "appliedDate", required = false) String appliedDateStr,
            @RequestParam(value = "status", required = false) String status,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtTokenProvider.getUsernameFromJWT(token.replace("Bearer ", ""));
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        Optional<Job> jobOpt = jobRepository.findById(jobId);

        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        if (jobOpt.isEmpty()) return ResponseEntity.badRequest().body("Job not found");

        try {
            Application application = new Application();
            application.setUser(userOpt.get());
            application.setJob(jobOpt.get());
            application.setResume(resumeFile.getBytes());
            application.setStatus(status != null ? status : "PENDING");
            application.setAppliedDate(appliedDateStr != null ? LocalDate.parse(appliedDateStr) : LocalDate.now());

            applicationRepository.save(application);
            return ResponseEntity.ok("Applied successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to apply: " + e.getMessage());
        }
    }

    @GetMapping("/applied-jobs")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ApplicationResponseDTO>> getAppliedJobs(Principal principal) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();

        List<ApplicationResponseDTO> response = applicationRepository
                .findByUserId(userOpt.get().getId())
                .stream()
                .map(app -> {
                    Job job = app.getJob();
                    return new ApplicationResponseDTO(
                        job.getId(),
                        job.getTitle(),
                        job.getCreatedBy().getEmail(),
                        app.getStatus(),
                        app.getAppliedDate()
                    );
                }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MyApplicationDTO>> getMyApplications(Principal principal) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();

        List<MyApplicationDTO> myApplications = applicationRepository.findByUserId(userOpt.get().getId())
                .stream()
                .map(app -> new MyApplicationDTO(
                        app.getId(),
                        app.getJob().getTitle(),
                        app.getJob().getCompany(),
                        app.getStatus()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(myApplications);
    }


    @GetMapping(value = "/resume/{applicationId}", produces = "application/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long applicationId) {
        return applicationRepository.findById(applicationId)
                .map(app -> ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=resume_" + applicationId + ".pdf")
                    .body(app.getResume()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/status/{applicationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long applicationId,
                                          @RequestParam("status") String status) {
        return applicationRepository.findById(applicationId)
                .map(app -> {
                    app.setStatus(status.toUpperCase());
                    applicationRepository.save(app);
                    return ResponseEntity.ok("Status updated");
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/delete/{applicationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteApplication(@PathVariable Long applicationId, Principal principal) {
        Optional<Application> optionalApp = applicationRepository.findById(applicationId);

        if (optionalApp.isEmpty()) return ResponseEntity.notFound().build();

        Application app = optionalApp.get();
        if (!app.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        applicationRepository.delete(app);
        return ResponseEntity.ok("Application deleted successfully.");
    }

    
    
    
    @PutMapping("/withdraw/{applicationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> withdrawApplication(@PathVariable Long applicationId, Principal principal) {
        Optional<Application> optionalApp = applicationRepository.findById(applicationId);

        if (optionalApp.isEmpty()) return ResponseEntity.notFound().build();

        Application app = optionalApp.get();
        if (!app.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        app.setStatus("WITHDRAWN");
        applicationRepository.save(app);
        return ResponseEntity.ok("Application withdrawn successfully.");
    }


}
