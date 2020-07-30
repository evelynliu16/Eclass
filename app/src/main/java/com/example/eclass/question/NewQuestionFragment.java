package com.example.eclass.question;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eclass.R;
import com.example.eclass.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * The fragment used to post a new question.
 */
public class NewQuestionFragment extends Fragment {

    View root;
    EditText summary, details;
    CheckBox anon;
    DatabaseReference database;
    Button post;
    User user;
    FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance().getReference("Question");
        mAuth = FirebaseAuth.getInstance();
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
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String phoneNumber = firebaseUser.getPhoneNumber();
                DatabaseReference userDataBase = FirebaseDatabase.getInstance().getReference("Student");
                userDataBase.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name;
                        user = snapshot.child(phoneNumber).getValue(User.class);
                        if (anon.isChecked()) {
                            name = "Anonymous";
                        } else {
                            name = user.getName();
                        }
                        Question newQues = new Question(name, summary.getText().toString(), details.getText().toString());
                        database.child(newQues.getId()).setValue(newQues);
                        user.postQuestion(newQues.getId());
                        QuestionListFragment fragment = new QuestionListFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.nav_host_fragment, fragment);
                        transaction.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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