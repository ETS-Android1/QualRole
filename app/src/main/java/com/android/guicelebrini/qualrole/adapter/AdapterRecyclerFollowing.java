package com.android.guicelebrini.qualrole.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRecyclerFollowing extends RecyclerView.Adapter<AdapterRecyclerFollowing.MyViewHolder> {

    private List<User> usersList;

    public AdapterRecyclerFollowing(List<User> usersList){
        this.usersList = usersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterRecyclerFollowing.MyViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.set(user);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textName, textFollowCode, textQuestionsNumber;
        private ImageView imageProfile;

        public MyViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.tv_user_name);
            textFollowCode = itemView.findViewById(R.id.tv_user_follow_code);
            textQuestionsNumber = itemView.findViewById(R.id.tv_user_questions_number);
            imageProfile = itemView.findViewById(R.id.iv_user_profile);

        }

        public void set(User user){
            this.textName.setText(user.getName());
            this.textFollowCode.setText("#" + user.getFollowCode());

            if (user.getQuestionsNumber() == 0){
                this.textQuestionsNumber.setText("Nenhuma pergunta feita");
            } else if (user.getQuestionsNumber() == 1 ) {
                this.textQuestionsNumber.setText(user.getQuestionsNumber() + " pergunta feita");
            } else {
                this.textQuestionsNumber.setText(user.getQuestionsNumber() + " perguntas feitas");
            }

            Picasso.get().load(user.getUrlProfileImage()).into(imageProfile);
        }
    }
}
