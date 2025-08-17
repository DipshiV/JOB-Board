package com.example.jwtlogin.request;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString(exclude = "resume")
public class ApplicationDTO {
	
    private Long jobId;
    @JsonIgnore
    private byte[] resume;
    private String status; // e.g., "PENDING", "ACCEPTED", "REJECTED"
    private LocalDate appliedDate;
}


