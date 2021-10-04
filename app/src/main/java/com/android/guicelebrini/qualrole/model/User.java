package com.android.guicelebrini.qualrole.model;

public class User {

    private String name;
    private String email;
    private String urlProfileImage;
    private int followCode;
    private int questionsNumber = 0;

    public User(){}

    public User(String name, String email, String urlProfileImage){
        this.name = name;
        this.email = email;
        this.urlProfileImage = urlProfileImage;
    }

    public User(String name, String email, int followCode, int questionsNumber) {
        this.name = name;
        this.email = email;
        this.followCode = followCode;
        this.questionsNumber = questionsNumber;
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

    public String getUrlProfileImage() {
        return urlProfileImage;
    }

    public void setUrlProfileImage(String urlProfileImage) {
        this.urlProfileImage = urlProfileImage;
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", followCode=" + followCode +
                ", questionsNumber=" + questionsNumber +
                '}';
    }
}
