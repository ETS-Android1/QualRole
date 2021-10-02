package com.android.guicelebrini.qualrole.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.adapter.AdapterRecyclerFollowing;
import com.android.guicelebrini.qualrole.model.User;

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

    private View view;

    public FollowingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_following, container, false);

        createUsersList();

        configureRecyclerFollowing();

        return view;
    }

    private void createUsersList(){
        usersList.add(new User("User1", "user1@123", 1234, 5));
        usersList.add(new User("User2", "user2@123", 4321, 0));
    }

    private void configureRecyclerFollowing(){
        recyclerFollowing = view.findViewById(R.id.recycler_following);
        adapter = new AdapterRecyclerFollowing(usersList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerFollowing.setLayoutManager(layoutManager);
        recyclerFollowing.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayout.VERTICAL));

        recyclerFollowing.setAdapter(adapter);
    }
}