package com.example.eclass.question;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eclass.R;
import com.example.eclass.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/** The fragment used to post a new question. */
public class newQuestionFragment extends Fragment {

    View root;
    EditText summary, details;
    CheckBox anon;
    DatabaseReference database;
    Button post;
    User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance().getReference("Question");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_new_question, container, false);

        summary = root.findViewById(R.id.newquesSummary);
        details = root.findViewById(R.id.newquesDescrip);
        anon = root.findViewById(R.id.newquesCheck);
        post = root.findViewById(R.id.newQuesPost);

        post.setOnClickListener(v -> {
            if (checkFields()) {
                String name;
                if (anon.isChecked()) {
                    name = "Anonymous";
                } else {
                    name = user.getName();
                }
                Question newQues = new Question(name, summary.getText().toString(), details.getText().toString());
                database.child(newQues.getId()).setValue(newQues);
            }
        });

        return root;
    }

    private boolean checkFields() {
        if (summary.getText().toString().equals("")) {
            String msg = "Please write a summary for your question";
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return false;
        } else if (details.getText().toString().equals("")) {
            String msg = "Please provide some details about your question";
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}