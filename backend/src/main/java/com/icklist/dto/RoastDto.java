package com.icklist.dto;

public class RoastDto {
    private String roastId;
    private String uploadBatchId;
    private String category;
    private String roastText;
    private String severity;
    private String emoji;
    private String createdAt;

    public RoastDto() {
    }

    public RoastDto(String roastId, String category, String roastText, String severity, String emoji) {
        this.roastId = roastId;
        this.category = category;
        this.roastText = roastText;
        this.severity = severity;
        this.emoji = emoji;
    }

    public RoastDto(String roastId, String uploadBatchId, String category, String roastText, String severity, String emoji, String createdAt) {
        this.roastId = roastId;
        this.uploadBatchId = uploadBatchId;
        this.category = category;
        this.roastText = roastText;
        this.severity = severity;
        this.emoji = emoji;
        this.createdAt = createdAt;
    }

    public String getUploadBatchId() {
        return uploadBatchId;
    }

    public void setUploadBatchId(String uploadBatchId) {
        this.uploadBatchId = uploadBatchId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRoastId() {
        return roastId;
    }

    public void setRoastId(String roastId) {
        this.roastId = roastId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRoastText() {
        return roastText;
    }

    public void setRoastText(String roastText) {
        this.roastText = roastText;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
