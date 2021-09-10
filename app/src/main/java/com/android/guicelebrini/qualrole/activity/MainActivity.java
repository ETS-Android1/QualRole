package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.activity.LoginActivity;
import com.android.guicelebrini.qualrole.adapter.AdapterRecyclerQuestions;
import com.android.guicelebrini.qualrole.model.Question;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private RecyclerView recyclerQuestions;
    private AdapterRecyclerQuestions adapter;
    private List<Question> questionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        configureToolbar();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        createQuestionsList();

        configureRecyclerQuestions();

        fab.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), AddQuestionActivity.class));
        });

    }

    private void findViewsById(){
        toolbar = findViewById(R.id.toolbar_main);
        fab = findViewById(R.id.fab_add_question);
        recyclerQuestions = findViewById(R.id.recycler_questions);
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);
    }

    private void createQuestionsList(){
        questionsList.add(new Question("Title", "User", "City", 25.60));
        questionsList.add(new Question("Title2", "User2", "City2", 0));
    }

    private void configureRecyclerQuestions(){
        adapter = new AdapterRecyclerQuestions(questionsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerQuestions.setLayoutManager(layoutManager);
        recyclerQuestions.setHasFixedSize(true);
        recyclerQuestions.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));

        recyclerQuestions.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_main ,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_infos:
                goToInfosActivity();
                return true;
            case R.id.action_logout:
                auth.signOut();
                googleLogout();
                goToLoginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void googleLogout(){
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(this,gso);
        googleSignInClient.signOut();
    }

    private void goToInfosActivity(){
        startActivity(new Intent(getApplicationContext(), InfosActivity.class));
    }

    private void goToLoginActivity(){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}