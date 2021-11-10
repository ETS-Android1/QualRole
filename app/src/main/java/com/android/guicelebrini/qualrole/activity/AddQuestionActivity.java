package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.helper.Preferences;
import com.android.guicelebrini.qualrole.model.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddQuestionActivity extends AppCompatActivity {

    private Button buttonAdd;
    private EditText editTitle, editDesc, editCity, editMoney;
    private TextInputLayout moneyLayout;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private Toolbar toolbar;

    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        findViewsById();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        preferences = new Preferences(getApplicationContext());
        configureToolbar();

        buttonAdd.setOnClickListener(view -> {
            addQuestionInFirebase();
        });

        moneyLayout.setEndIconOnClickListener(view -> {
            editMoney.setText("00.00");
        });

    }

    private void findViewsById(){
        buttonAdd = findViewById(R.id.button_add_question);
        editTitle = findViewById(R.id.edit_question_title);
        editDesc = findViewById(R.id.edit_question_desc);
        editCity = findViewById(R.id.edit_question_city);
        editMoney = findViewById(R.id.edit_question_money);

        moneyLayout = findViewById(R.id.textInputLayout4);

        toolbar = findViewById(R.id.toolbar_add_question);
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);
        toolbar.setTitle("Adicione uma pergunta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addQuestionInFirebase(){
        String title = editTitle.getText().toString();
        String description = editDesc.getText().toString();
        String city = editCity.getText().toString();
        String insertedMoney = editMoney.getText().toString();
        String encodedEmail = Base64Custom.encode(user.getEmail());

        String questionUser = user.getDisplayName() + " " + preferences.getFollowCode();

        if (title.equals("") || description.equals("") || city.equals("") || insertedMoney.equals("")){
            Toast.makeText(getApplicationContext(), "Por favor, insira valores válidos", Toast.LENGTH_SHORT).show();
        } else {
            double money = Double.parseDouble(insertedMoney);
            Question question = new Question(title, description, questionUser, city, money);

            db.collection("questions").add(question)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            DocumentReference reference = task.getResult();
                            String questionId = reference.getId();
                            db.collection("users").document(encodedEmail)
                                    .collection("questions").document(questionId).set(question)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            db.collection("users").document(encodedEmail).update("questionsNumber", FieldValue.increment(1))
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()){
                                                            Toast.makeText(getApplicationContext(), "Pergunta salva com sucesso", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Falha ao salvar pergunta", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Falha ao salvar pergunta", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        
    }
}