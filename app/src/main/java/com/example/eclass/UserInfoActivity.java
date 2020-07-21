package com.example.eclass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoActivity extends AppCompatActivity {

    EditText name, password;
    CheckBox instructor;
    Button register;
    String phone;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        name = findViewById(R.id.rgName);
        register = findViewById(R.id.rgRegister);
        instructor = findViewById(R.id.rgInstructor);

        phone = getIntent().getStringExtra("phoneNumber");

        mDatabase = FirebaseDatabase.getInstance().getReference("Student");

        //Register the user and direct to main activity.
        register.setOnClickListener(v -> {
            if (name.getText().toString().equals("")) {
                String msg = "Please enter your name";
                Toast.makeText(UserInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().equals("")) {
                String msg = "Please set your password";
                Toast.makeText(UserInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                saveUser(name.getText().toString(), phone, instructor.isChecked());
            }
        });
    }


    //Save the user information to firebase to direct to main activity.
    private void saveUser(String name, String phone, boolean instructor) {
        User user = new User(name, phone, instructor);
        mDatabase.child(phone).setValue(user);
        Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
        intent.putExtra("phoneNumber", phone);
        startActivity(intent);
    }
}