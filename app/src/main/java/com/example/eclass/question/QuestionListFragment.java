package com.example.eclass.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eclass.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class QuestionListFragment extends Fragment {
    View root;
    RecyclerView mRecyclerView;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_question_list, container, false);

        mRecyclerView = root.findViewById(R.id.queslistRecycle);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Question");

        setHasOptionsMenu(true);

        /*
        Question question6 = new Question("Liu", "Test question", "this is for the purpose of test only to make sure that using an arraylist for answers work");
        databaseReference.child(question6.getId()).setValue(question6);
         */


        return root;
    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Question> options = new FirebaseRecyclerOptions.Builder<Question>()
                .setQuery(databaseReference, Question.class).build();

        FirebaseRecyclerAdapter<Question, QuestionHolder> adapter
                = new FirebaseRecyclerAdapter<Question, QuestionHolder>(options) {
            @NonNull
            @Override
            public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.question_card, parent, false);

                return new QuestionHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull QuestionHolder questionViewHolder, int i, @NonNull Question question) {
                questionViewHolder.setQuestion(question.getTitle(), question.getDescription(), question.answered());


                // Set on click listener for view holder. //
                questionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", question.getId());
                        QuestionFragment fragment = new QuestionFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.nav_host_fragment, fragment);
                        transaction.commit();
                    }
                });
            }
        };

        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.question, menu);
        MenuItem item = menu.findItem(R.id.quesSearch);
        MenuItem add = menu.findItem(R.id.quesAdd);

        // Set on click listener for search button in toolbar. //
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });

        // Set on click listener for add button in toolbar. //
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                newQuestionFragment fragment = new newQuestionFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.nav_host_fragment, fragment);
                transaction.commit();
                return true;
            }
        });
    }

    /** Search through the database after search view activated. */
    private void firebaseSearch(String text) {
        String query = text.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<Question> options = new FirebaseRecyclerOptions.Builder<Question>()
                .setQuery(firebaseQuery, Question.class).build();

        FirebaseRecyclerAdapter<Question, QuestionHolder> searchAdapter =
                new FirebaseRecyclerAdapter<Question, QuestionHolder>(options) {
                    @NonNull
                    @Override
                    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getActivity())
                                .inflate(R.layout.question_card, parent, false);

                        return new QuestionHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull QuestionHolder questionViewHolder, int i, @NonNull Question question) {
                        questionViewHolder.setQuestion(question.getTitle(), question.getDescription(), question.answered());


                        // Set on click listener for view holder. //
                        questionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id", question.getId());
                                QuestionFragment fragment = new QuestionFragment();
                                fragment.setArguments(bundle);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.addToBackStack(null);
                                transaction.replace(R.id.nav_host_fragment, fragment);
                                transaction.commit();
                            }
                        });
                    }
                };

        searchAdapter.startListening();
        mRecyclerView.setAdapter(searchAdapter);
    }
}