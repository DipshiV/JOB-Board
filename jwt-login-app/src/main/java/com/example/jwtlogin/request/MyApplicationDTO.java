package com.example.jwtlogin.request;

public class MyApplicationDTO {
    private Long applicationId;
    private String jobTitle;
    private String company;
    private String status;

    public MyApplicationDTO(Long applicationId, String jobTitle, String company, String status) {
        this.applicationId = applicationId;
        this.jobTitle = jobTitle;
        this.company = company;
        this.status = status;
    }

    // Getters and Setters

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
