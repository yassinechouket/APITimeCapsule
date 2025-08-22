package com.chouket370.apitimecapsule.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor

@Builder
public class EmailResponse {
    private boolean success;
    private String message;
    private String jobId;
    private String jobGroup;


    public EmailResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public EmailResponse(boolean success, String jobId, String jobGroup, String message) {
        this.success = success;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
        this.message = message;

    }
}
