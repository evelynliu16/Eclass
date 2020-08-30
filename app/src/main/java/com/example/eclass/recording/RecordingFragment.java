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

import static com.example.eclass.MainActivity.fab;
import static com.example.eclass.MainActivity.user;


public class RecordingFragment extends Fragment {

    RecyclerView mRecyclerView;
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

        databaseReference = FirebaseDatabase.getInstance().getReference("Video");

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Video> options = new FirebaseRecyclerOptions.Builder<Video>()
                .setQuery(databaseReference, Video.class).build();

        if (user != null && user.isInstructor()) {
            fab.setVisibility(View.VISIBLE);
        }

        FirebaseRecyclerAdapter<Video, RecordingHolder> firebaseRecycleAdapter =
                new FirebaseRecyclerAdapter<Video, RecordingHolder>(options) {
                    @NonNull
                    @Override
                    public RecordingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getActivity())
                                .inflate(R.layout.recording_card, parent, false);

                        return new RecordingHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull RecordingHolder recordingHolder, int i, @NonNull Video model) {
                        recordingHolder.setExoplayer(getActivity().getApplication(), model.getTitle(), model.getUrl());
                    }
                };

        firebaseRecycleAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecycleAdapter);


        fab.setOnClickListener(v -> {
            UploadRecordingFragment fragment = new UploadRecordingFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment, fragment);
            transaction.commit();
        });

    }

    /**
     * Implement search view.
     **/
    private void firebaseSearch(String text) {
        String query = text.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<Video> options = new FirebaseRecyclerOptions.Builder<Video>()
                .setQuery(firebaseQuery, Video.class).build();

        FirebaseRecyclerAdapter<Video, RecordingHolder> firebaseRecycleAdapter =
                new FirebaseRecyclerAdapter<Video, RecordingHolder>(options) {
                    @NonNull
                    @Override
                    public RecordingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recording_card, parent, false);

                        return new RecordingHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull RecordingHolder recordingHolder, int i, @NonNull Video model) {
                        recordingHolder.setExoplayer(getActivity().getApplication(), model.getTitle(), model.getUrl());
                    }
                };
        firebaseRecycleAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecycleAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recording, menu);
        MenuItem search = menu.findItem(R.id.recSearch);
        SearchView searchView = (SearchView) search.getActionView();
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

