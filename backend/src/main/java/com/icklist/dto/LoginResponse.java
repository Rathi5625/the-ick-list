package com.icklist.dto;

public class LoginResponse {
    private String token;
    private String userId;
    private String name;
    private String avatarInitials;
    private String avatarColor;

    public LoginResponse() {
    }

    public LoginResponse(String token, String userId, String name, String avatarInitials, String avatarColor) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.avatarInitials = avatarInitials;
        this.avatarColor = avatarColor;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
