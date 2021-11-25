package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.adapter.AdapterRecyclerAnswers;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.helper.Preferences;
import com.android.guicelebrini.qualrole.helper.RecyclerItemClickListener;
import com.android.guicelebrini.qualrole.model.Answer;
import com.android.guicelebrini.qualrole.model.Question;
import com.android.guicelebrini.qualrole.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private TextView textTitle, textUser, textDesc, textCity, textMoney, textNoAnswers;
    private EditText editAnswer;
    private ImageButton buttonAddAnswer;
    private ImageView imageUser;
    private Toolbar toolbar;

    private String firestoreQuestionId;

    private FirebaseFirestore db;
    private FirebaseUser user;

    private RecyclerView recyclerAnswers;
    private AdapterRecyclerAnswers adapter;
    private List<Answer> answersList = new ArrayList<>();

    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        findViewsById();
        configureToolbar();
        getExtras();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        preferences = new Preferences(getApplicationContext());

        getQuestionInFirebase();

        verifyList();

        configureRecyclerView();

        buttonAddAnswer.setOnClickListener(view -> {
            addAnswerInFirebase();
        });
    }

    private void findViewsById(){
        textTitle = findViewById(R.id.tv_question_title);
        textUser = findViewById(R.id.tv_question_user);
        textDesc = findViewById(R.id.tv_question_desc);
        textCity = findViewById(R.id.tv_question_city);
        textMoney = findViewById(R.id.tv_question_money);
        textNoAnswers = findViewById(R.id.tv_question_no_answers);

        editAnswer = findViewById(R.id.edit_question_answer);

        buttonAddAnswer = findViewById(R.id.button_question_add_answer);

        imageUser = findViewById(R.id.iv_question_user);

        toolbar = findViewById(R.id.toolbar_question);

        recyclerAnswers = findViewById(R.id.recycler_answers);
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

    public void goToAddAnswerActivity(){
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
                        answer.setFirestoreId(snapshot.getId());
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

        recyclerAnswers.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerAnswers, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Answer answer = answersList.get(position);
                String userEmail = user.getEmail();
                int deletePosition = position;

                if (answer.getUserEmail().equals(userEmail)){
                    openDeleteDialog(firestoreQuestionId, answer.getFirestoreId(), deletePosition);
                }
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

        recyclerAnswers.setAdapter(adapter);
    }

    private void openDeleteDialog(String firestoreQuestionId, String firestoreAnswerId, int deletePosition) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(QuestionActivity.this);

        dialog.setTitle("Excluir resposta");
        dialog.setMessage("Você tem certeza que deseja excluir sua resposta?");
        dialog.setCancelable(false);


        dialog.setPositiveButton("Excluir", (dialogInterface, i) -> {
            deleteAnswer(firestoreQuestionId, firestoreAnswerId, deletePosition);
        });

        dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {

        });

        dialog.create().show();
    }

    private void deleteAnswer(String firestoreQuestionId, String firestoreAnswerId, int deletePosition) {

        db.collection("questions").document(firestoreQuestionId)
                .collection("answers").document(firestoreAnswerId).delete().addOnSuccessListener(unused -> {
                    answersList.remove(deletePosition);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Resposta excluída com sucesso", Toast.LENGTH_SHORT).show();
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_question, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_add_comment:
                goToAddAnswerActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void addAnswerInFirebase(){
        String description = editAnswer.getText().toString();

        if (description.equals("") || description.equals(null)){
            Toast.makeText(getApplicationContext(), "Por favor, insira valores válidos", Toast.LENGTH_SHORT).show();
        } else {

            String userName = user.getDisplayName() + " " + preferences.getFollowCode();
            String userEmail = user.getEmail();

            Answer answer = new Answer(description, userName, userEmail);
            db.collection("questions").document(firestoreQuestionId).collection("answers").add(answer)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Resposta salva com sucesso", Toast.LENGTH_SHORT).show();
                            createAnswersList();
                            editAnswer.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro ao salvar resposta", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        createAnswersList();
    }
}