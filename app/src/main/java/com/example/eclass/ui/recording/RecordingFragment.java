package com.example.eclass.ui.recording;

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

import java.util.ArrayList;

public class RecordingFragment extends Fragment {

    RecyclerView mRecyclerView;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    View root;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_recording, container, false);
        mRecyclerView = root.findViewById(R.id.recRecycle);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Video");

        /*
        RecordingAdapter ra = new RecordingAdapter(this.getActivity(), createArrayList(20));
        mRecyclerView.setAdapter(ra);
        */

        return root;
    }

    @Override
    public void onStart() {

        super.onStart();
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(databaseReference, Model.class).build();

        FirebaseRecyclerAdapter<Model, RecordingHolder> firebaseRecycleAdapter =
                new FirebaseRecyclerAdapter<Model, RecordingHolder>(options) {
                    @NonNull
                    @Override
                    public RecordingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getActivity())
                                .inflate(R.layout.recording_card, (ViewGroup) root, false);

                        return new RecordingHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull RecordingHolder recordingHolder, int i, @NonNull Model model) {
                        recordingHolder.setExoplayer(getActivity().getApplication(), model.getName(), model.getUrl());
                    }
                };
        firebaseRecycleAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecycleAdapter);
    }

    private ArrayList<Model> createArrayList(int size) {

        ArrayList<Model> result = new ArrayList();
        for (int i = 1; i <= size; i++) {
            Model ci = new Model();

            result.add(ci);

        }

        return result;
    }

}