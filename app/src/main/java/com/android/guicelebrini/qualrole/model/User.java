package com.android.guicelebrini.qualrole.model;

public class User {

    private String name;
    private String email;
    private int followCode;
    private int questionsNumber = 0;

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

    public int getFollowCode() {
        return followCode;
    }

    public void setFollowCode(int followCode) {
        this.followCode = followCode;
    }

    public int getQuestionsNumber() {
        return questionsNumber;
    }

    public void setQuestionsNumber(int questionsNumber) {
        this.questionsNumber = questionsNumber;
    }
}
