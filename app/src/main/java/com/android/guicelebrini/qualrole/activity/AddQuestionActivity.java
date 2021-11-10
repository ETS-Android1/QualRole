package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.api.LocationService;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.helper.Preferences;
import com.android.guicelebrini.qualrole.model.City;
import com.android.guicelebrini.qualrole.model.Question;
import com.android.guicelebrini.qualrole.model.State;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddQuestionActivity extends AppCompatActivity {

    private Button buttonAdd;
    private EditText editTitle, editDesc, editMoney;
    private TextInputLayout moneyLayout;
    private AutoCompleteTextView dropdownStates, dropdownCities;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private Toolbar toolbar;

    private Preferences preferences;

    private Retrofit retrofit;
    private String urlApiIbge = "https://servicodados.ibge.gov.br/api/v1/localidades/";
    private String state;
    private String completeAdress = "";

    private List<State> statesList = new ArrayList<>();
    private List<City> citiesList = new ArrayList<>();

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

        retrofit = new Retrofit.Builder()
                .baseUrl(urlApiIbge)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getStatesRetrofit();

        buttonAdd.setOnClickListener(view -> {
            addQuestionInFirebase();
        });

        moneyLayout.setEndIconOnClickListener(view -> {
            editMoney.setText("00.00");
        });

        dropdownStates.setOnItemClickListener((parent, view, position, l) -> {
            dropdownCities.setText("");
            String selectedState = parent.getItemAtPosition(position).toString();
            getCitiesFromStateRetrofit(selectedState);
            state = selectedState;
        });

        dropdownCities.setOnItemClickListener((parent, view, position, l) -> {
            String selectedCity = parent.getItemAtPosition(position).toString();
            completeAdress = selectedCity + ", " + state;
        });

    }

    private void findViewsById(){
        buttonAdd = findViewById(R.id.button_add_question);
        editTitle = findViewById(R.id.edit_question_title);
        editDesc = findViewById(R.id.edit_question_desc);
        editMoney = findViewById(R.id.edit_question_money);

        moneyLayout = findViewById(R.id.textInputLayout4);

        dropdownStates = findViewById(R.id.dropdown_states);
        dropdownCities = findViewById(R.id.dropdown_cities);

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
        String insertedMoney = editMoney.getText().toString();
        String encodedEmail = Base64Custom.encode(user.getEmail());

        String questionUser = user.getDisplayName() + " " + preferences.getFollowCode();

        if (title.equals("") || description.equals("") || completeAdress.equals("") || insertedMoney.equals("")){
            Toast.makeText(getApplicationContext(), "Por favor, insira valores válidos", Toast.LENGTH_SHORT).show();
        } else {
            double money = Double.parseDouble(insertedMoney);
            Question question = new Question(title, description, questionUser, completeAdress, money);

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

    private void getStatesRetrofit(){

        LocationService locationService = retrofit.create(LocationService.class);
        Call<List<State>> statesCall = locationService.getStates();

        statesCall.enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> call, Response<List<State>> response) {
                statesList = response.body();

                putInDropdownStates(statesList);

                for (int i = 0; i < statesList.size(); i++){

                    State state = statesList.get(i);
                    Log.i("Estados", state.toString());

                }
            }

            @Override
            public void onFailure(Call<List<State>> call, Throwable t) {

            }
        });

    }

    private void putInDropdownStates(List<State> statesList){
        ArrayAdapter<State> adapter = new ArrayAdapter<State>(getApplicationContext(), R.layout.dropdown_item, statesList);
        adapter.setDropDownViewResource(R.layout.dropdown_item);

        dropdownStates.setAdapter(adapter);
    }

    private void getCitiesFromStateRetrofit(String stateInitials) {
        LocationService locationService = retrofit.create(LocationService.class);
        Call<List<City>> citiesCall = locationService.getCitiesFromState(stateInitials);

        citiesCall.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful()){
                    citiesList = response.body();

                    putInDropdownCities(citiesList);

                    for (int i = 0; i < citiesList.size(); i++){

                        City city = citiesList.get(i);
                        Log.i("Cidade", city.toString());

                    }

                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {

            }
        });
    }

    private void putInDropdownCities(List<City> citiesList){
        ArrayAdapter<City> adapter = new ArrayAdapter<City>(getApplicationContext(), R.layout.dropdown_item, citiesList);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        dropdownCities.setAdapter(adapter);
    }

}