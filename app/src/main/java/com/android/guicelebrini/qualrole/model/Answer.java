package com.android.guicelebrini.qualrole.model;

public class Answer {

    private String description;
    private String user;

    public Answer() {
    }

    public Answer(String description, String user) {
        this.description = description;
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
