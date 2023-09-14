package com.spots.common.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserBody {
    private String id;
    private String username;

    @Email(message = "Email must be valid!")
    private String email;

    @Size(min = 5, max = 15, message = "Password should be between 5 and 15 characters!")
    private String password;

    private String picture;

    private long timeUntilNextRoll;

    private long currentSpotId;

    public long getCurrentSpotId() {
        return currentSpotId;
    }

    public void setCurrentSpotId(long currentSpotId) {
        this.currentSpotId = currentSpotId;
    }

    public long getTimeUntilNextRoll() {
        return timeUntilNextRoll;
    }

    public void setTimeUntilNextRoll(long timeUntilNextRoll) {
        this.timeUntilNextRoll = timeUntilNextRoll;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
