package com.icklist.dto;

public class UploadResponse {
    private String uploadBatchId;
    private int transactionCount;

    public UploadResponse() {
    }

    public UploadResponse(String uploadBatchId, int transactionCount) {
        this.uploadBatchId = uploadBatchId;
        this.transactionCount = transactionCount;
    }

    public String getUploadBatchId() {
        return uploadBatchId;
    }

    public void setUploadBatchId(String uploadBatchId) {
        this.uploadBatchId = uploadBatchId;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
}
