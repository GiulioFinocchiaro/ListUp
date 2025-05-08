package com.giuliofinocchiaro.listup.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private int userId;
    private String displayName;

    public LoggedInUser(int userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public int getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "LoggedInUser{" +
                "userId=" + userId +
                ", displayName='" + displayName + '\'' +
                "}";
    }
}