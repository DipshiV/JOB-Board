package com.example.jwtlogin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;


@Data
@AllArgsConstructor
public class ApplicationResponseDTO {
    private Long jobId;
    private String jobTitle;
    private String recruiterEmail;  // from job.getCreatedBy().getEmail()
    private String status;          // from app.getStatus()
    private LocalDate appliedDate;  // from app.getAppliedDate()
}
