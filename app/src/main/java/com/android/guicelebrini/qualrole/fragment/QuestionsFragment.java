package com.android.guicelebrini.qualrole.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.activity.QuestionActivity;
import com.android.guicelebrini.qualrole.adapter.AdapterRecyclerQuestions;
import com.android.guicelebrini.qualrole.helper.RecyclerItemClickListener;
import com.android.guicelebrini.qualrole.model.Question;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionsFragment extends Fragment {

    private View view;

    private FirebaseFirestore db;

    private RecyclerView recyclerQuestions;
    private AdapterRecyclerQuestions adapter;
    private List<Question> questionsList = new ArrayList<>();

    private String loggedUserId = "";


    public QuestionsFragment() {
        // Required empty public constructor
    }

    public QuestionsFragment(String loggedUserId){
        this.loggedUserId = loggedUserId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_questions, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerQuestions = view.findViewById(R.id.recycler_questions);

        configureRecyclerQuestions();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        createQuestionsList();
    }

    private void createQuestionsList(){

        CollectionReference reference;

        if (loggedUserId.equals("")){
            reference = db.collection("questions");
        } else {
            reference = db.collection("users").document(loggedUserId).collection("questions");
        }

        reference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    questionsList.clear();

                    for (DocumentSnapshot snapshot : task.getResult()) {
                        //Question question = snapshot.toObject(Question.class);
                        String title = snapshot.getString("title");
                        String description = snapshot.getString("description");
                        String user = snapshot.getString("user");
                        String city = snapshot.getString("city");
                        double moneyAvailable = snapshot.getDouble("moneyAvailable");

                        Question question = new Question();
                        question.setTitle(title);
                        question.setDescription(description);
                        question.setUser(user);
                        question.setCity(city);
                        question.setMoneyAvailable(moneyAvailable);
                        question.setFirestoreId(snapshot.getId());
                        questionsList.add(question);
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    Log.e("questionsList", e.getMessage());
                    Toast.makeText(view.getContext(), "Falha ao recuperar perguntas", Toast.LENGTH_SHORT);
                });
    }

    private void configureRecyclerQuestions(){

        if (loggedUserId.equals("")){
            adapter = new AdapterRecyclerQuestions(questionsList, 0);
        } else {
            adapter = new AdapterRecyclerQuestions(questionsList, 1);
        }


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());

        recyclerQuestions.setLayoutManager(layoutManager);
        recyclerQuestions.setHasFixedSize(true);
        //recyclerQuestions.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayout.VERTICAL));

        recyclerQuestions.addOnItemTouchListener(new RecyclerItemClickListener(view.getContext(), recyclerQuestions, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Question question = questionsList.get(position);

                Intent intent = new Intent(view.getContext(), QuestionActivity.class);
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
}