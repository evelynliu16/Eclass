package com.example.eclass.recording;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.example.eclass.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;
import static com.example.eclass.MainActivity.fab;


public class UploadRecordingFragment extends Fragment {

    private static final int PiCK_VIDEO = 1;
    Uri video_uri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    View root;
    EditText video_title;
    VideoView videoView;
    ImageView icon;
    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_upload_recording, container, false);

        fab.setVisibility(View.GONE);
        video_title = root.findViewById(R.id.uploadTitle);
        icon = root.findViewById(R.id.uploadIcon);
        videoView = root.findViewById(R.id.uploadVid);
        progressBar = root.findViewById(R.id.uploadProgress);
        progressBar.setVisibility(View.GONE);

        videoView.setZOrderMediaOverlay(true);
        MediaController mediaController = new MediaController(requireContext());
        videoView.setMediaController(mediaController);
        videoView.start();

        Button choose_video = root.findViewById(R.id.uploadChoose);
        Button upload = root.findViewById(R.id.uploadUpload);

        databaseReference = FirebaseDatabase.getInstance().getReference("Video");
        storageReference = FirebaseStorage.getInstance().getReference("Video");

        choose_video.setOnClickListener(v -> chooseVideo());
        upload.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            uploadVideo(video_uri);
        });

        return root;
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video"), PiCK_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Called after picking from camera of gallery
        if (resultCode == RESULT_OK && requestCode == PiCK_VIDEO && data != null) {
            // Image picked from gallery and update image uri
            fab.setVisibility(View.GONE);

            video_uri = data.getData();
            videoView.setVideoURI(video_uri);
            icon.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            video_title.setVisibility(View.VISIBLE);
        }
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadVideo(Uri uri) {
        String title = video_title.getText().toString();
        String search = title.toLowerCase();

        if (video_uri != null || !TextUtils.isEmpty(title)) {
            StorageReference storageReference2nd = storageReference.child(System.currentTimeMillis() + "." + getExt(video_uri));
            UploadTask uploadTask = storageReference2nd.putFile(uri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return storageReference2nd.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Video video = new Video(downloadUri.toString(), title, search);
                    String i = databaseReference.push().getKey();
                    databaseReference.child(i).setValue(video);

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), "Video uploaded successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(requireContext(), "Upload failed. Please try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}