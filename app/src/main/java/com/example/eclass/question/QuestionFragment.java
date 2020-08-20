package com.example.eclass.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eclass.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class QuestionFragment extends Fragment {

    private DatabaseReference firebaseDatabase;
    private TextView title, name, description, answer;
    private EditText newAnswer;
    private Question question;
    private Button post, goBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Question");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_question, container, false);

        title = root.findViewById(R.id.quesTitle);
        name = root.findViewById(R.id.quesName);
        description = root.findViewById(R.id.quesDescrip);
        answer = root.findViewById(R.id.quesAnswer);
        newAnswer = root.findViewById(R.id.quesNewAnswer);
        post = root.findViewById(R.id.quesPost);

        Bundle bundle = this.getArguments();
        String id = bundle.getString("id");

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (id != null) {
                    question = snapshot.child(id).getValue(Question.class);
                    setContent();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }

    private void setContent() {
        title.setText(question.getTitle());
        String posted = "posted by " + question.getName();
        name.setText(posted);
        description.setText(question.getDescription());
        if (!question.answered()) {
            answer.setText(R.string.ques_no_answer_posted);
        } else {
            String answers = question.getAnswer();
            answer.setText(answers);
        }

        post.setOnClickListener(v -> {
            String text = newAnswer.getText().toString();
            if (text.equals("")) {
                String msg = "Please type your answer in the answer box";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            } else {
                question.postAnswer(text);
                firebaseDatabase.child(question.getId()).child("answer").setValue(question.getAnswer());
                String newText = question.getAnswer();
                answer.setText(newText);
            }
        });
    }
}