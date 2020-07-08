package com.example.eclass.ui.recording;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eclass.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.text.BreakIterator;

public class RecordingHolder extends RecyclerView.ViewHolder {

    SimpleExoPlayer exoPlayer;
    PlayerView playerView;

    public RecordingHolder(@NonNull View itemView) {
        super(itemView);

    }

    public void setExoplayer(Application application, String title, String url) {
        TextView text = itemView.findViewById(R.id.recTitle);
        playerView = itemView.findViewById(R.id.recVideo);

        text.setText(title);

        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(application).build();
            TrackSelector trackSelector = new DefaultTrackSelector(application, new AdaptiveTrackSelection.Factory());
            exoPlayer = new SimpleExoPlayer.Builder(application).build();
            Uri video = Uri.parse(url);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory).createMediaSource(video);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            Log.e("ViewHolder", "exoplayer error" + e.toString());
        }
    }
}
