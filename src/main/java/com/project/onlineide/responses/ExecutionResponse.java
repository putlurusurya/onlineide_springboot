package com.project.onlineide.responses;

import lombok.Data;

@Data
public class ExecutionResponse {
    private boolean success;
    private String output;
    private String jobId;
    public ExecutionResponse(boolean success, String output, String jobId) {
        this.success = success;
        this.output = output;
        this.jobId = jobId;
    }
}
