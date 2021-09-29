package com.android.guicelebrini.qualrole.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.helper.Preferences;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class InfosActivity extends AppCompatActivity {

    private ShapeableImageView imageProfile;
    private TextView textName, textEmail, textFollowCode;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String followCode;

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
        textFollowCode = findViewById(R.id.tv_follow_code);
    }

    private void getUserFromFirebase(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void setInfos(FirebaseUser user) {
        Preferences preferences = new Preferences(getApplicationContext());

        Picasso.get().load(user.getPhotoUrl()).into(imageProfile);
        textName.setText(user.getDisplayName());
        textEmail.setText(user.getEmail());
        textFollowCode.setText(preferences.getFollowCode());
    }
}