package com.android.guicelebrini.qualrole.model;

public class Question {

    private String title;
    private String description;
    private String user;
    private String city;
    private String moneyAvailable;

    public Question(){
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMoneyAvailable() {
        return moneyAvailable;
    }

    public void setMoneyAvailable(String moneyAvailable) {
        this.moneyAvailable = moneyAvailable;
    }
}
