package com.example.jobportal.vo;

public class EmployerJobs {
    String userName;
    String jobsId;
    String jobTitle;
    String jobDescription;

    public EmployerJobs() {
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
