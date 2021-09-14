package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.model.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class QuestionActivity extends AppCompatActivity {

    private TextView textTitle, textUser, textDesc, textCity, textMoney;

    private String firestoreQuestionId;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        findViewsById();
        getExtras();

        db = FirebaseFirestore.getInstance();

        getQuestionInFirebase();
    }

    private void findViewsById(){
        textTitle = findViewById(R.id.tv_question_title);
        textUser = findViewById(R.id.tv_question_user);
        textDesc = findViewById(R.id.tv_question_desc);
        textCity = findViewById(R.id.tv_question_city);
        textMoney = findViewById(R.id.tv_question_money);
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
                        Question firestoreQuestion = snapshot.toObject(Question.class);
                        set(firestoreQuestion);
                    }
                });
    }

    private void set(Question question) {
        textTitle.setText(question.getTitle());
        textUser.setText(question.getUser());
        textDesc.setText(question.getDescription());
        textCity.setText(question.getCity());

        if (question.getMoneyAvailable() > 0) {
            this.textMoney.setText(String.format("%.2f",question.getMoneyAvailable()));
        } else {
            this.textMoney.setText("----");
            this.textMoney.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_money_off_24, 0, 0, 0);
        }
    }
}