package com.android.guicelebrini.qualrole.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.model.Answer;

import java.util.List;

public class AdapterRecyclerAnswers extends RecyclerView.Adapter<AdapterRecyclerAnswers.MyViewHolder> {

    private List<Answer> answersList;

    public AdapterRecyclerAnswers(List<Answer> answersList){
        this.answersList = answersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answers_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterRecyclerAnswers.MyViewHolder holder, int position) {
        Answer answer = answersList.get(position);
        holder.set(answer);
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textDesc, textUser;

        public MyViewHolder(View itemView) {
            super(itemView);

            textDesc = itemView.findViewById(R.id.tv_answer_description);
            textUser = itemView.findViewById(R.id.tv_answer_user);

        }

        private void set(Answer answer){
            this.textDesc.setText(answer.getDescription());
            this.textUser.setText(answer.getUser());
        }
    }
}
