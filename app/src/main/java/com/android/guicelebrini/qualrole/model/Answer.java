package com.android.guicelebrini.qualrole.model;

import com.google.firebase.firestore.Exclude;

public class Answer {

    private String description;
    private String user;
    private String userEmail;
    private String firestoreId;

    public Answer() {
    }

    public Answer(String description, String user, String userEmail) {
        this.description = description;
        this.user = user;
        this.userEmail = userEmail;
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

    @Exclude
    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }
}
