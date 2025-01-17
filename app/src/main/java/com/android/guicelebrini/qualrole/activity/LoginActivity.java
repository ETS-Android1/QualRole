package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.helper.Preferences;
import com.android.guicelebrini.qualrole.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private SignInButton googleButton;

    private FirebaseFirestore db;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewsById();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        createRequest();

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void findViewsById(){
        googleButton = findViewById(R.id.google_button);
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i("Resultado", "Google sign in succeeded. Your id is: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.i("Resultado", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Resultado", "signInWithCredential in Firebase:success");
                            firebaseUser = mAuth.getCurrentUser();
                            createUserInFirebaseFirestore(firebaseUser);
                            updateUI(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("Resultado", "signInWithCredential in Firebase:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void createUserInFirebaseFirestore(FirebaseUser firebaseUser){
        String userName = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        String encodedEmail = Base64Custom.encode(userEmail);
        String urlProfileImage = firebaseUser.getPhotoUrl().toString();

        db.collection("users").document(encodedEmail).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot reference = task.getResult();
                    if (reference.exists()){
                        Log.i("Resultado", "The user already exists");
                        Preferences preferences = new Preferences(getApplicationContext());
                        User user = reference.toObject(User.class);
                        preferences.saveData("#" + user.getFollowCode());
                        updateFirestoreInfos(encodedEmail, userName, urlProfileImage);
                    } else {
                        User user = new User(userName, userEmail, urlProfileImage);
                        createFollowCode(user);
                    }
                });

    }

    private void updateFirestoreInfos(String encodedEmail, String userName, String urlProfileImage) {
        db.collection("users").document(encodedEmail).update("name", userName, "urlProfileImage", urlProfileImage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("Resultado", "Informations updated succesfully");
                    } else {
                        Log.i("Resultado", "Informations updated failed");
                    }
                });

    }


    private void createFollowCode(User user){

        HashSet<Integer> set = new HashSet<>();

        db.collection("users").get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        User caughtUser = snapshot.toObject(User.class);
                        set.add(caughtUser.getFollowCode());
                    }

                    int registeredUsers = set.size();
                    Log.i("Resultado", String.valueOf(registeredUsers));
                    int sortedNumber = new Random().nextInt(9000) + 1000;

                    while (set.size() == registeredUsers) {
                        set.add(sortedNumber);

                        if(set.size() == registeredUsers) {
                            Log.i("Resultado","The number couldn't be saved");
                            sortedNumber = new Random().nextInt(9000) + 1000;
                        } else {
                            Log.i("Resultado","Your new number is " + sortedNumber);
                            user.setFollowCode(sortedNumber);
                            saveInFirestore(user);
                            Preferences preferences = new Preferences(getApplicationContext());
                            preferences.saveData("#" + sortedNumber);
                        }
                    }

                });
    }

    private void saveInFirestore(User user){

        String encodedEmail = Base64Custom.encode(user.getEmail());

        db.collection("users").document(encodedEmail).set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.i("Resultado", "User saved in Firestore successfully");
                    } else {
                        Log.i("Resultado", "Your user couldn't be saved");
                    }
                });
    }

    private void updateUI(FirebaseUser firebaseUser) {
        if(firebaseUser != null){
            Toast.makeText(this,"Usuário logado no app com sucesso",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(firebaseUser);
    }
}