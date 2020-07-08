package com.example.eclass.ui.recording;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eclass.R;

import java.util.ArrayList;
import java.util.List;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingHolder> {

    Context c;
    ArrayList<Model> recordings;

    public RecordingAdapter(Context c, ArrayList<Model> models) {
        this.c = c;
        this.recordings = models;
    }
    @NonNull
    @Override
    public RecordingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recording_card, parent, false);

        return new RecordingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingHolder holder, int position) {
        /*
        RecordingHolder.title.setText(recordings.get(position).getTitle());
        */
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }
}
