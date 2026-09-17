package com.icklist.dto;

import jakarta.validation.constraints.NotBlank;

public class GenerateRoastRequest {
    @NotBlank(message = "uploadBatchId is required")
    private String uploadBatchId;

    public GenerateRoastRequest() {
    }

    public GenerateRoastRequest(String uploadBatchId) {
        this.uploadBatchId = uploadBatchId;
    }

    public String getUploadBatchId() {
        return uploadBatchId;
    }

    public void setUploadBatchId(String uploadBatchId) {
        this.uploadBatchId = uploadBatchId;
    }
}
