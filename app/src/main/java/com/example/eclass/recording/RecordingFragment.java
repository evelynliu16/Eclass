package com.example.eclass.recording;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eclass.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


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

        setHasOptionsMenu(true);

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
                        recordingHolder.setExoplayer(getActivity().getApplication(), model.getTitle(), model.getUrl());
                    }
                };

        firebaseRecycleAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecycleAdapter);
    }

    /** Implement search view. **/
    private void firebaseSearch(String text) {
        String query = text.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(firebaseQuery, Model.class).build();

        FirebaseRecyclerAdapter<Model, RecordingHolder> firebaseRecycleAdapter =
                new FirebaseRecyclerAdapter<Model, RecordingHolder>(options) {
                    @NonNull
                    @Override
                    public RecordingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recording_card, parent, false);

                        return new RecordingHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull RecordingHolder recordingHolder, int i, @NonNull Model model) {
                        recordingHolder.setExoplayer(getActivity().getApplication(), model.getTitle(), model.getUrl());
                    }
                };
        firebaseRecycleAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecycleAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recording, menu);
        MenuItem item = menu.findItem(R.id.recSearch);

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
    }
}