package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.helper.Preferences;
import com.android.guicelebrini.qualrole.model.Answer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddAnswerActivity extends AppCompatActivity {

    private Button buttonAdd;
    private EditText editDesc;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String questionFirestoreId;

    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer);
        findViewsById();
        getExtras();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        preferences = new Preferences(getApplicationContext());

        buttonAdd.setOnClickListener(view -> {
            addAnswerInFirebase();
        });
    }

    private void findViewsById(){
        buttonAdd = findViewById(R.id.button_add_answer);
        editDesc = findViewById(R.id.edit_answer_desc);
    }

    private void getExtras(){
        Bundle extras = getIntent().getExtras();
        questionFirestoreId = extras.getString("firestoreId");
    }

    private void addAnswerInFirebase(){
        String description = editDesc.getText().toString();

        if (description.equals("") || description.equals(null)){
            Toast.makeText(getApplicationContext(), "Por favor, insira valores válidos", Toast.LENGTH_SHORT).show();
        } else {

            String userName = user.getDisplayName() + " " + preferences.getFollowCode();
            String userEmail = user.getEmail();

            Answer answer = new Answer(description, userName, userEmail);
            db.collection("questions").document(questionFirestoreId).collection("answers").add(answer)
                    .addOnCompleteListener(task -> {
                       if (task.isSuccessful()){
                           Toast.makeText(getApplicationContext(), "Resposta salva com sucesso", Toast.LENGTH_SHORT).show();
                           finish();
                       } else {
                           Toast.makeText(getApplicationContext(), "Erro ao salvar resposta", Toast.LENGTH_SHORT).show();
                       }
                    });

        }
    }
}