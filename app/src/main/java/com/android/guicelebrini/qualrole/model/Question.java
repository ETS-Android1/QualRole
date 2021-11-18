package com.android.guicelebrini.qualrole.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;

public class Question {

    private String title;
    private String description;
    private String user;
    private String userEmail;
    private String city;
    private double moneyAvailable;
    private String firestoreId;
    private FieldValue createdAt;

    public Question(){
    }

    public Question(String title, String description, String user, String userEmail, String city, double moneyAvailable) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.userEmail = userEmail;
        this.city = city;
        this.moneyAvailable = moneyAvailable;
        this.createdAt = FieldValue.serverTimestamp();
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    @Exclude
    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public FieldValue getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(FieldValue createdAt) {
        this.createdAt = createdAt;
    }
}
