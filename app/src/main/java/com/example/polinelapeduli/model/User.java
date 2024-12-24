package com.example.polinelapeduli.model;

import com.example.polinelapeduli.utils.Enum.ELoginMethod;
import com.example.polinelapeduli.utils.Enum.ERole;

import org.jetbrains.annotations.NotNull;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private ELoginMethod loginMethod;
    private ERole role;
    private String profilePicture;
    private boolean isActive = true;
    private String createdAt;
    private String updatedAt;

    public User() {}

    public User(int userId, String fullName, String email, ELoginMethod loginMethod, ERole role, String profilePicture, boolean isActive, String createdAt, String updatedAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.loginMethod = loginMethod;
        this.role = role;
        this.profilePicture = profilePicture;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ELoginMethod getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(ELoginMethod loginMethod) {
        this.loginMethod = loginMethod;
    }

    public ERole getRole() {
        return role;
    }

    public void setRole(ERole role) {
        this.role = role;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    @NotNull
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", loginMethod=" + loginMethod +
                ", loginMethod=" + role +
                ", profilePicture='" + profilePicture + '\'' +
                ", isActive=" + isActive +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}

