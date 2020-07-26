package com.example.jobportal.vo;

import java.util.Map;

public class ApplicantDetail {

    private String contact;
    private String jobStatus;
    private String firstName;
    private Map<String, String> dateTime;
    private String lastName;


    public Map<String, String> getDateTime() {
        return dateTime;
    }

    public void setDateTime(Map<String, String> dateTime) {
        this.dateTime = dateTime;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "ApplicantDetail{" +
                "contact='" + contact + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                '}';
    }
}
