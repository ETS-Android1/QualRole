package com.android.guicelebrini.qualrole.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.adapter.AdapterRecyclerFollowing;
import com.android.guicelebrini.qualrole.helper.Base64Custom;
import com.android.guicelebrini.qualrole.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment {

    private RecyclerView recyclerFollowing;
    private AdapterRecyclerFollowing adapter;
    private List<User> usersList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseUser user;

    private View view;

    public FollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_following, container, false);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        configureRecyclerFollowing();

        return view;
    }

    private void createUsersList(){

        String encodedEmail = Base64Custom.encode(user.getEmail());

        db.collection("users").document(encodedEmail).collection("followedUsers").get()
                .addOnCompleteListener(task -> {

                    usersList.clear();

                    for (DocumentSnapshot snapshot : task.getResult()) {
                        User user = snapshot.toObject(User.class);
                        String followedUserId = Base64Custom.encode(user.getEmail());

                        getFollowedUserFromFirestore(followedUserId, usersList, new GetCompleteCallback() {
                            @Override
                            public void complete(List<User> updatedList) {
                                usersList = updatedList;
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }

                });


    }

    public interface GetCompleteCallback{
        void complete(List<User> updatedList);
    }

    private void getFollowedUserFromFirestore(String followedUserId, List<User> usersList, GetCompleteCallback callback){

        db.collection("users").document(followedUserId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        User user = snapshot.toObject(User.class);
                        usersList.add(user);
                        callback.complete(usersList);
                    }
                });

    }

    private void configureRecyclerFollowing(){
        recyclerFollowing = view.findViewById(R.id.recycler_following);
        adapter = new AdapterRecyclerFollowing(usersList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerFollowing.setLayoutManager(layoutManager);
        recyclerFollowing.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayout.VERTICAL));

        recyclerFollowing.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        createUsersList();
    }
}