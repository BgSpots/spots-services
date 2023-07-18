package com.spots.controller;

class ApiSuccess {
    public String action;
    public String message;

    public String getAction() {
        return action;
    }

    public ApiSuccess(String action, String message) {
        this.action = action;
        this.message = message;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
