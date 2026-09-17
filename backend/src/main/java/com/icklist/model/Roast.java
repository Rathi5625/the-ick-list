package com.icklist.model;

import java.time.Instant;

public class Roast {
    private String userId;
    private String roastId;
    private String uploadBatchId;
    private String category;
    private String roastText;
    private String severity;
    private String emoji;
    private String createdAt;

    public Roast() {
    }

    public Roast(String userId, String roastId, String uploadBatchId, String category, String roastText, String severity, String emoji, String createdAt) {
        this.userId = userId;
        this.roastId = roastId;
        this.uploadBatchId = uploadBatchId;
        this.category = category;
        this.roastText = roastText;
        this.severity = severity;
        this.emoji = emoji;
        this.createdAt = createdAt != null ? createdAt : Instant.now().toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoastId() {
        return roastId;
    }

    public void setRoastId(String roastId) {
        this.roastId = roastId;
    }

    public String getUploadBatchId() {
        return uploadBatchId;
    }

    public void setUploadBatchId(String uploadBatchId) {
        this.uploadBatchId = uploadBatchId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
