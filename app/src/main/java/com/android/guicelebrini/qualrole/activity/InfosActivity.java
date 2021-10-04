package com.android.guicelebrini.qualrole.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.fragment.QuestionsFragment;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.helper.Preferences;
import com.android.guicelebrini.qualrole.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class InfosActivity extends AppCompatActivity {

    private ShapeableImageView imageProfile;
    private TextView textName, textEmail, textFollowCode;

    private FirebaseFirestore db;
    private FirebaseUser googleUser;

    private String firestoreId;
    private boolean isFromLoggedUser;

    private QuestionsFragment questionsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        findViewsById();

        db = FirebaseFirestore.getInstance();
        googleUser = FirebaseAuth.getInstance().getCurrentUser();

        Bundle extras = getIntent().getExtras();

        if (extras != null){
            isFromLoggedUser = false;
            firestoreId = extras.getString("firestoreId");
        } else {
            isFromLoggedUser = true;
            firestoreId = Base64Custom.encode(googleUser.getEmail());
        }

        getUserFromFirebase(firestoreId);
    }

    private void findViewsById(){
        imageProfile = findViewById(R.id.iv_profile);
        textName = findViewById(R.id.tv_profile_name);
        textEmail = findViewById(R.id.tv_profile_email);
        textFollowCode = findViewById(R.id.tv_follow_code);
    }

    private void getUserFromFirebase(String firestoreId){

        db.collection("users").document(firestoreId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    setInfos(user);
                });

    }

    private void setInfos(User user) {

        Picasso.get().load(user.getUrlProfileImage()).into(imageProfile);
        textName.setText(user.getName());

        if (isFromLoggedUser){
            textEmail.setText(user.getEmail());
        } else {
            textEmail.setVisibility(View.GONE);
        }

        textFollowCode.setText("#" + user.getFollowCode());

        loadQuestionsList(firestoreId);
    }

    private void loadQuestionsList(String firestoreId) {
        questionsFragment = new QuestionsFragment(firestoreId);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameQuestionsFragment, questionsFragment);
        transaction.commit();

    }
}