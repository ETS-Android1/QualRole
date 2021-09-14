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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.activity.LoginActivity;
import com.android.guicelebrini.qualrole.adapter.AdapterRecyclerQuestions;
import com.android.guicelebrini.qualrole.helper.RecyclerItemClickListener;
import com.android.guicelebrini.qualrole.model.Question;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

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
        db = FirebaseFirestore.getInstance();

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

        db.collection("questions").get()
                .addOnCompleteListener(task -> {
                    questionsList.clear();

                    for (DocumentSnapshot snapshot : task.getResult()) {
                        Question question = snapshot.toObject(Question.class);
                        question.setFirestoreId(snapshot.getId());
                        questionsList.add(question);
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    Log.e("questionsList", e.getMessage());
                    Toast.makeText(getApplicationContext(), "Falha ao recuperar perguntas", Toast.LENGTH_SHORT);
                });
    }

    private void configureRecyclerQuestions(){
        adapter = new AdapterRecyclerQuestions(questionsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerQuestions.setLayoutManager(layoutManager);
        recyclerQuestions.setHasFixedSize(true);
        recyclerQuestions.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));

        recyclerQuestions.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerQuestions, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Question question = questionsList.get(position);

                Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                intent.putExtra("firestoreId", question.getFirestoreId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

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

    @Override
    protected void onStart() {
        super.onStart();
        createQuestionsList();
    }
}