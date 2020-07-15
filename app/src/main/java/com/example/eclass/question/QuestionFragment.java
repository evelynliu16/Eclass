package com.example.eclass.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eclass.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class QuestionFragment extends Fragment {
    View root;
    RecyclerView mRecyclerView;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_question, container, false);
        mRecyclerView = root.findViewById(R.id.quesRecycle);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Question");

        setHasOptionsMenu(true);


        return root;
    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Question> options = new FirebaseRecyclerOptions.Builder<Question>()
                .setQuery(databaseReference, Question.class).build();

        FirebaseRecyclerAdapter<Question, QuestionViewHolder> adapter
                = new FirebaseRecyclerAdapter<Question, QuestionViewHolder>(options) {
            @NonNull
            @Override
            public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.question_card, parent, false);

                return new QuestionViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull QuestionViewHolder questionViewHolder, int i, @NonNull Question question) {
                questionViewHolder.setQuestion(question.getTitle(), question.getDescription(), question.answered());
            }
        };

        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

}