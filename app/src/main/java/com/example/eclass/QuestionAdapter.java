package com.example.eclass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter {
    public TextView mTitle;
    public TextView mDescrip;
    public CheckBox mCheck;
    private ArrayList<Question> mQuestions;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.quesTitle);
            mDescrip = itemView.findViewById(R.id.quesDescrip);
            mCheck = itemView.findViewById(R.id.quesCheckbox);
        }
    }

    public QuestionAdapter(ArrayList<Question> questions) {
        mQuestions = questions;
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View questionView = inflater.inflate(R.layout.question_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(questionView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question question = mQuestions.get(position);
        String descrip = question.getDescription();
        if (descrip.length() > 75) {
            descrip = question.getDescription().substring(0, 97) + "...";
        }

        mTitle.setText(question.getTitle());
        mDescrip.setText(descrip);
        mCheck.setChecked(question.answered());
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }
}
