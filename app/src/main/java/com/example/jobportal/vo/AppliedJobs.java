package com.example.jobportal.vo;

public class AppliedJobs {
    private String jobId;
    private String jobStatus;
    private String jobTitle;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Override
    public String toString() {
        return "AppliedJobs{" +
                "jobId='" + jobId + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                '}';
    }


}
