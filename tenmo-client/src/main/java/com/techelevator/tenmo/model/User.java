package com.techelevator.tenmo.model;

public class User {

    private Long userId;
    private String username;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User otherUser = (User) other;
            return otherUser.getUserId().equals(userId)
                    && otherUser.getUsername().equals(username);
        } else {
            return false;
        }
    }
}
