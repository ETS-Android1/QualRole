package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.adapter.AdapterRecyclerAnswers;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.model.Answer;
import com.android.guicelebrini.qualrole.model.Question;
import com.android.guicelebrini.qualrole.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private TextView textTitle, textUser, textDesc, textCity, textMoney, textNoAnswers;
    private ImageView imageUser;

    private String firestoreQuestionId;

    private FirebaseFirestore db;

    private RecyclerView recyclerAnswers;
    private AdapterRecyclerAnswers adapter;
    private List<Answer> answersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        findViewsById();
        getExtras();

        db = FirebaseFirestore.getInstance();

        getQuestionInFirebase();

        verifyList();

        configureRecyclerView();
    }

    private void findViewsById(){
        textTitle = findViewById(R.id.tv_question_title);
        textUser = findViewById(R.id.tv_question_user);
        textDesc = findViewById(R.id.tv_question_desc);
        textCity = findViewById(R.id.tv_question_city);
        textMoney = findViewById(R.id.tv_question_money);
        textNoAnswers = findViewById(R.id.tv_question_no_answers);

        imageUser = findViewById(R.id.iv_question_user);

        recyclerAnswers = findViewById(R.id.recycler_answers);
    }

    private void getExtras(){
        Bundle extras = getIntent().getExtras();
        firestoreQuestionId = extras.getString("firestoreId");
    }

    private void getQuestionInFirebase(){
        db.collection("questions").document(firestoreQuestionId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        Question firestoreQuestion = new Question();

                        String title = snapshot.getString("title");
                        String description = snapshot.getString("description");
                        String user = snapshot.getString("user");
                        String city = snapshot.getString("city");
                        double moneyAvailable = snapshot.getDouble("moneyAvailable");

                        firestoreQuestion.setTitle(title);
                        firestoreQuestion.setDescription(description);
                        firestoreQuestion.setUser(user);
                        firestoreQuestion.setCity(city);
                        firestoreQuestion.setMoneyAvailable(moneyAvailable);
                        firestoreQuestion.setFirestoreId(snapshot.getId());

                        String userEmail = snapshot.getString("userEmail");
                        getUserQuestionInFirebase(userEmail);

                        set(firestoreQuestion);
                    }
                });
    }

    private void set(Question question) {
        textTitle.setText(question.getTitle());
        textDesc.setText(question.getDescription());
        textCity.setText(question.getCity());

        if (question.getMoneyAvailable() > 0) {
            this.textMoney.setText(String.format("%.2f",question.getMoneyAvailable()));
        } else {
            this.textMoney.setText("----");
            this.textMoney.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_money_off_24, 0, 0, 0);
        }
    }

    private void getUserQuestionInFirebase(String userEmail){
        String userId = Base64Custom.encode(userEmail);

        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            User firestoreUser = documentSnapshot.toObject(User.class);
            set(firestoreUser);
        });
    }

    private void set(User user){
        textUser.setText(user.getName() + " #" + user.getFollowCode());

        Picasso.get().load(user.getUrlProfileImage()).into(imageUser);
    }

    public void goToAddAnswerActivity(View view){
        Intent intent = new Intent(getApplicationContext(), AddAnswerActivity.class);
        intent.putExtra("firestoreId", firestoreQuestionId);
        startActivity(intent);
    }

    private void createAnswersList(){
        db.collection("questions").document(firestoreQuestionId).collection("answers").get()
                .addOnCompleteListener(task -> {

                    answersList.clear();

                    for ( DocumentSnapshot snapshot : task.getResult()) {
                        Answer answer = snapshot.toObject(Answer.class);
                        answersList.add(answer);
                    }
                    verifyList();

                    adapter.notifyDataSetChanged();

                });
    }


    private void verifyList(){

        if (!answersList.isEmpty()) {
            textNoAnswers.setVisibility(View.GONE);
        } else {
            textNoAnswers.setVisibility(View.VISIBLE);
        }
    }

    private void configureRecyclerView(){
        adapter = new AdapterRecyclerAnswers(answersList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerAnswers.setLayoutManager(layoutManager);
        recyclerAnswers.setHasFixedSize(true);

        recyclerAnswers.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createAnswersList();
    }
}