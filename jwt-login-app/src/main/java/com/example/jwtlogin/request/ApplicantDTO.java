package com.example.jwtlogin.request;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantDTO {

    private Long applicationId;
    private String name;
    private String email;
    private String phone;
    private String status;

    public ApplicantDTO(Long applicationId, String name, String email, String phone, String status) {
        this.applicationId = applicationId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    // Getters and setters
}
