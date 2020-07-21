package com.example.eclass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phone, code;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String TAG;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference("Student");

        phone = findViewById(R.id.lgEditPhone);
        code = findViewById(R.id.lgCode);
        Button signinButton = findViewById(R.id.lgSignIn);
        Button registerButton = findViewById(R.id.lgRegister);
        Button sendVerif = findViewById(R.id.lgSend);

        sendVerif.setOnClickListener(v -> {
            if (phone.getText().toString().equals("")) {
                String msg = "Please enter your phone number";
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String number = phone.getText().toString();
                    if (!snapshot.child(number).exists()) {
                        Toast.makeText(LoginActivity.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                    } else {
                        sendMsg(number);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        // Sign in. //
        signinButton.setOnClickListener(v -> {
            if (phone.getText().toString().equals("")) {
                String msg = "Please enter your phone number";
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else if (code.getText().toString().equals("")) {
                String msg = "Please enter the verification code";
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                verifyVerificationCode(code.getText().toString());
            }
        });

        // Register. //
        registerButton.setOnClickListener(v -> {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        });

    }

    /**
     * Send the verification code to the user.
     **/
    private void sendMsg(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+1" + number, 60, TimeUnit.SECONDS, this, mCallbacks);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String verfification = credential.getSmsCode();
            if (verfification != null) {
                Log.w(TAG, "Verification Completed");
                code.setText(verfification);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "Verification Failed", e);
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "Code sent" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    /**
     * Verify the code sent to the user.
     **/
    private void verifyVerificationCode(String code) {
        mAuth = FirebaseAuth.getInstance();

        PhoneAuthCredential crendential = PhoneAuthProvider.getCredential(mVerificationId, code);
        verifyWithPhoneAuthCredential(crendential);
    }

    /**
     * Verify the user. Register and sign in if the code is correct, display error messages otherwise.
     **/
    private void verifyWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // Verify the user. Otherwise display an error message.
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("phoneNumber", phone.getText().toString());

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        //verification unsuccessful.. display an error message
                        String message = "Something is wrong, we will fix it soon";

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Incorrect code entered";
                        }
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}