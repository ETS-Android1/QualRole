package com.android.guicelebrini.qualrole.model;

public class User {

    private String name;
    private String email;
    private String followCode;

    public User(){}

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFollowCode() {
        return followCode;
    }

    public void setFollowCode(String followCode) {
        this.followCode = followCode;
    }
}
