package com.example.eclass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText phone, password;
    private Button signinButton, registerButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference("Student");

        phone = findViewById(R.id.lgEditPhone);
        password = findViewById(R.id.lgEditPassword);
        signinButton = findViewById(R.id.lgSignIn);
        registerButton = findViewById(R.id.lgRegister);

        signinButton.setOnClickListener(v -> {
            if (phone.getText().toString().equals("")) {
                String msg = "Please enter your phone number";
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().equals("")) {
                String msg = "Please enter your password";
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                final String phoneNumber = phone.getText().toString();
                final String PIN = password.getText().toString();

                mDatabase.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child(phoneNumber).exists()) {
                            String msg = "You are not registered";
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            User user = snapshot.child(phoneNumber).getValue(User.class);
                            if (PIN.equals(user.getPassword())) {
                                String welcome = getString(R.string.welcome) + user.getName();
                                Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

                                goToMainActivity();
                            } else {
                                String msg = "Incorrect password";
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void goToRegisterActivity() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}