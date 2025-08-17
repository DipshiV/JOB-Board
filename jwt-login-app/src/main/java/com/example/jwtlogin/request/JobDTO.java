package com.example.jwtlogin.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JobDTO {
    private String title;
    private String description;
    private String company;
    private String location;
    private double salary;
    private LocalDate postedDate;
}
