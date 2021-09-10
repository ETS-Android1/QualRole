package com.android.guicelebrini.qualrole.model;

public class Question {

    private String title;
    private String description;
    private String user;
    private String city;
    private double moneyAvailable;

    public Question(){
    }

    public Question(String title, String user, String city, double moneyAvailable) {
        this.title = title;
        this.user = user;
        this.city = city;
        this.moneyAvailable = moneyAvailable;
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

    public double getMoneyAvailable() {
        return moneyAvailable;
    }

    public void setMoneyAvailable(double moneyAvailable) {
        this.moneyAvailable = moneyAvailable;
    }
}
