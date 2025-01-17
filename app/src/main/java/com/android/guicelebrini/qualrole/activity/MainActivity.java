package com.android.guicelebrini.qualrole.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.activity.LoginActivity;
import com.android.guicelebrini.qualrole.adapter.AdapterRecyclerQuestions;
import com.android.guicelebrini.qualrole.fragment.FollowingFragment;
import com.android.guicelebrini.qualrole.fragment.QuestionsFragment;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.helper.RecyclerItemClickListener;
import com.android.guicelebrini.qualrole.model.Question;
import com.android.guicelebrini.qualrole.model.User;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private ViewPager viewPager;
    private static final int FOLLOWING_TAB_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        configureToolbar();
        configureTabLayout();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        fab.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), AddQuestionActivity.class));
        });

    }

    private void findViewsById(){
        toolbar = findViewById(R.id.toolbar_main);
        fab = findViewById(R.id.fab_add_question);
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);
    }

    private void configureTabLayout(){
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.fragment_questions, QuestionsFragment.class)
                .add(R.string.fragment_following, FollowingFragment.class)
                .create());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.tab_layout_main);
        viewPagerTab.setViewPager(viewPager);
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
            case R.id.action_follow:
                openFollowDialog();
                return true;
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

    private void openFollowDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

        dialog.setTitle("Comece a seguir alguém!");
        dialog.setMessage("Digite o nome do usuário e o código");
        dialog.setCancelable(false);

        EditText editUserName = new EditText(this);
        editUserName.setHint("Ex: Usuário#9999");
        editUserName.setPadding(40, 20, 40, 20);

        dialog.setView(editUserName);

        dialog.setPositiveButton("Adicionar", (dialogInterface, i) -> {
            verifyFollowedUser(editUserName);
        });

        dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {

        });

        dialog.create().show();
    }

    private void verifyFollowedUser(EditText editText){
        db = FirebaseFirestore.getInstance();
        String encodedEmail = Base64Custom.encode(user.getEmail());

        String followedUser = editText.getText().toString();

        if (followedUser.equals("")) {
            Toast.makeText(getApplicationContext(), "Não foi possível encontrar um usuário com essas informações", Toast.LENGTH_SHORT).show();
        } else {
            db.collection("users").get()
                    .addOnCompleteListener(task -> {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            User user = snapshot.toObject(User.class);
                            String followedUserFirebase = user.getName() + "#" + user.getFollowCode();

                            if (followedUser.equals(followedUserFirebase)){
                                saveFollowedUser(encodedEmail, user.getEmail());
                            }
                        }
                    });
        }
    }

    private void saveFollowedUser(String userId, String followedUserEmail){
        HashMap<String, Object> map = new HashMap<>();
        String encodedFollowedUserEmail = Base64Custom.encode(followedUserEmail);

        map.put("email", followedUserEmail);

        db.collection("users").document(userId).collection("followedUsers").document(encodedFollowedUserEmail).set(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Usuário seguido com sucesso", Toast.LENGTH_SHORT).show();
                        FollowingFragment fragment = (FollowingFragment) viewPager
                                .getAdapter()
                                .instantiateItem(viewPager, FOLLOWING_TAB_INDEX);
                        fragment.createUsersList();

                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao seguir usuário", Toast.LENGTH_SHORT).show();
                    }
                });
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