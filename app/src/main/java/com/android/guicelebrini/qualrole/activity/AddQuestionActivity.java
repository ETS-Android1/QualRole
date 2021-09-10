package com.android.guicelebrini.qualrole.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.guicelebrini.qualrole.R;

public class AddQuestionActivity extends AppCompatActivity {

    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        findViewsById();

        buttonAdd.setOnClickListener(view -> {
            addQuestionInFirebase();
        });

    }

    private void findViewsById(){
        buttonAdd = findViewById(R.id.button_add_question);
    }

    private void addQuestionInFirebase(){
        
    }
}