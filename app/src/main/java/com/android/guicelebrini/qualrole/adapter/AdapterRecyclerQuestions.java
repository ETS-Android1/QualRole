package com.android.guicelebrini.qualrole.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.guicelebrini.qualrole.R;
import com.android.guicelebrini.qualrole.model.Question;

import java.util.List;

public class AdapterRecyclerQuestions extends RecyclerView.Adapter<AdapterRecyclerQuestions.MyViewHolder> {

    private List<Question> questionsList;

    public AdapterRecyclerQuestions(List<Question> questionsList){
        this.questionsList = questionsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_questions_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterRecyclerQuestions.MyViewHolder holder, int position) {
        Question question = questionsList.get(position);
        holder.set(question);
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textTitle, textUser, textCity, textMoney;

        public MyViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.tv_title);
            textUser = itemView.findViewById(R.id.tv_user);
            textCity = itemView.findViewById(R.id.tv_city);
            textMoney = itemView.findViewById(R.id.tv_money);
        }

        public void set(Question question){
            this.textTitle.setText(question.getTitle());
            this.textUser.setText(question.getUser());
            this.textCity.setText(question.getCity());
            this.textMoney.setText(String.valueOf(question.getMoneyAvailable()));
        }
    }
}
