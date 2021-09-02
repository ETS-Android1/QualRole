package com.android.guicelebrini.qualrole.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.guicelebrini.qualrole.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class InfosActivity extends AppCompatActivity {

    private ShapeableImageView imageProfile;
    private TextView textName, textEmail;

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        findViewsById();

        getUserFromFirebase();

        setInfos(user);
    }

    private void findViewsById(){
        imageProfile = findViewById(R.id.iv_profile);
        textName = findViewById(R.id.tv_profile_name);
        textEmail = findViewById(R.id.tv_profile_email);
    }

    private void getUserFromFirebase(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void setInfos(FirebaseUser user) {
        Picasso.get().load(user.getPhotoUrl()).into(imageProfile);
        textName.setText(user.getDisplayName());
        textEmail.setText(user.getEmail());
    }
}