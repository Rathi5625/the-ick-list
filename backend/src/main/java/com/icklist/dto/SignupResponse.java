package com.icklist.dto;

public class SignupResponse {
    private String userId;
    private String email;
    private String name;
    private String avatarInitials;
    private String avatarColor;

    public SignupResponse() {
    }

    public SignupResponse(String userId, String email, String name, String avatarInitials, String avatarColor) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.avatarInitials = avatarInitials;
        this.avatarColor = avatarColor;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarInitials() {
        return avatarInitials;
    }

    public void setAvatarInitials(String avatarInitials) {
        this.avatarInitials = avatarInitials;
    }

    public String getAvatarColor() {
        return avatarColor;
    }

    public void setAvatarColor(String avatarColor) {
        this.avatarColor = avatarColor;
    }
}
