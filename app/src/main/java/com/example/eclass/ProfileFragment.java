package com.example.eclass;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;
import static com.example.eclass.MainActivity.mDatabase;
import static com.example.eclass.MainActivity.toolbar;
import static com.example.eclass.MainActivity.user;

/**
 * Fragment used to change profile picture and name
 **/
public class ProfileFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_Pick_Gallery_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    ProgressBar pd;
    String[] cameraPermissions;
    String[] storagePermissions;

    ImageView pic;

    // uri of picked image
    Uri image_uri;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        EditText name = root.findViewById(R.id.pfName);
        pic = root.findViewById(R.id.pfImage);
        Button bt2 = root.findViewById(R.id.pfButton);
        Button upload = root.findViewById(R.id.pfUpload);
        pd = root.findViewById(R.id.pfProgress);
        pd.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference("Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Student");

        cameraPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        toolbar.setTitle("Profile");

        name.setText(user.getName());

        if (user.getProfilePic() != null) {
            Picasso.get().load(user.getProfilePic()).into(pic);
        }

        // Change name. //
        bt2.setOnClickListener(v -> mDatabase.child(user.getPhone()).child("name").setValue(name.getText().toString()));

        //Upload profile picture. //
        upload.setOnClickListener(v -> showImagePicDialog());

        return root;
    }

    /**
     * Image dialog that allow users to pick from camera and gallery.
     **/
    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }

            }
        });
        builder.create().show();
    }

    /**
     * Check storage permission.
     **/
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Request storage permissoin.
     **/
    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    /**
     * Check camera permisson.
     **/
    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    /**
     * Request camera permission.
     **/
    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    /**
     * Used when requesting permissions. Called when user press allow or deny from permission request dialog.
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // For camera permissions. //
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(requireActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            // For storage permission. //
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(requireActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    /**
     * Pick the profile picture from gallery.
     **/
    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_Pick_Gallery_CODE);
    }

    /**
     * Pick the profile picture camera.
     **/
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Called after picking from camera of gallery
        if (resultCode == RESULT_OK) {
            // Image picked from gallery and update image uri
            if (requestCode == IMAGE_Pick_Gallery_CODE) {
                image_uri = data.getData();
            }
            pd.setVisibility(View.VISIBLE);
            uploadProfilePhoto(image_uri);
        }
    }

    /**
     * Upload profile image to the database.
     **/
    private void uploadProfilePhoto(Uri uri) {

        StorageReference storageReference2nd = storageReference.child(user.getPhone());

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
                databaseReference.child(user.getPhone()).child("profilePic").setValue(downloadUri.toString());
                user.setProfilePic(downloadUri.toString());
                Picasso.get().load(user.getProfilePic()).into(pic);
                pd.setVisibility(View.GONE);
            } else {
                Toast.makeText(requireContext(), "Some error occurred, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}