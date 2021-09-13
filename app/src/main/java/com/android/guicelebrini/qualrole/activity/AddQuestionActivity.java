package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.model.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddQuestionActivity extends AppCompatActivity {

    private Button buttonAdd;
    private EditText editTitle, editDesc, editCity, editMoney;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        findViewsById();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        buttonAdd.setOnClickListener(view -> {
            addQuestionInFirebase();
        });

    }

    private void findViewsById(){
        buttonAdd = findViewById(R.id.button_add_question);
        editTitle = findViewById(R.id.edit_question_title);
        editDesc = findViewById(R.id.edit_question_desc);
        editCity = findViewById(R.id.edit_question_city);
        editMoney = findViewById(R.id.edit_question_money);
    }

    private void addQuestionInFirebase(){
        String title = editTitle.getText().toString();
        String description = editDesc.getText().toString();
        String city = editCity.getText().toString();
        String insertedMoney = editMoney.getText().toString();
        double money = Double.parseDouble(insertedMoney);

        String encodedEmail = Base64Custom.encode(user.getEmail());

        if (title.equals("") || description.equals("") || city.equals("") || insertedMoney.equals("")){
            Toast.makeText(getApplicationContext(), "Por favor, insira valores válidos", Toast.LENGTH_SHORT).show();
        } else {
            Question question = new Question(title, description, user.getDisplayName(), city, money);

            db.collection("questions").add(question)
                    .addOnCompleteListener(task -> {
                        DocumentReference reference = task.getResult();
                        String questionId = reference.getId();
                        db.collection("users").document(encodedEmail).collection("questions").document(questionId).set(question);
                    });







        }
        
    }
}