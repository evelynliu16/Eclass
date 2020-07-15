package com.example.eclass.question;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eclass.R;

public class QuestionViewHolder extends RecyclerView.ViewHolder{

    TextView mTitle, mDescrip;
    CheckBox answered;

    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.quesTitle);
        mDescrip = itemView.findViewById(R.id.quesDescrip);
        answered = itemView.findViewById(R.id.quesCheckbox);
    }

    public void setQuestion(String title, String description, boolean answer) {
        mTitle.setText(title);
        mDescrip.setText(description);
        answered.setChecked(answer);
    }
}
