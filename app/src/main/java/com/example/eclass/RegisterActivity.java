package com.example.eclass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
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

public class RegisterActivity extends AppCompatActivity {

    EditText number, OTP;
    Button sendOPT, verify;

    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String TAG;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        number = findViewById(R.id.rgPhone);
        OTP = findViewById(R.id.rgCode);
        sendOPT = findViewById(R.id.rgSendCode);
        verify = findViewById(R.id.rgVerify);

        TAG = "RegisterActivity";

        mDatabase = FirebaseDatabase.getInstance().getReference("Student");

        // Send the verification code if a valid phone number is provided. //
        sendOPT.setOnClickListener(v -> {
            final String phoneNumber = number.getText().toString();

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(phoneNumber).exists()) {
                        Toast.makeText(RegisterActivity.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        if (phoneNumber.length() == 10) {
                            Context context = getApplicationContext();
                            CharSequence text = "Sending message...";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            sendMsg(phoneNumber);
                        } else {
                            String message = "Please enter a valid phone number";
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        // Verify and register the user. //
        verify.setOnClickListener(v -> {
            if (OTP.getText().toString().equals("")) {
                String msg = "Please enter your verification code";
                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                String code = OTP.getText().toString();
                verifyVerificationCode(code);
            }
        });
    }

    /** Send the verification code to the user. **/
    private void sendMsg(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+1" + number, 60, TimeUnit.SECONDS, this, mCallbacks);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if (code != null) {
                Log.w(TAG, "Verification Completed");
                OTP.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
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

    /** Verify the code sent to the user. **/
    private void verifyVerificationCode(String code) {
        mAuth = FirebaseAuth.getInstance();

        PhoneAuthCredential crendential = PhoneAuthProvider.getCredential(mVerificationId, code);
        verifyWithPhoneAuthCredential(crendential);
    }

    /** Verify the user. Register and sign in if the code is correct, display error messages otherwise. **/
    private void verifyWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // Verify the user. Otherwise display an error message.
        mAuth.signInWithCredential(credential).addOnCompleteListener(RegisterActivity.this,
                task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, UserInfoActivity.class);
                        intent.putExtra("phoneNumber", number.getText().toString());
                        intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        //verification unsuccessful.. display an error message
                        String message = "Something is wrong, we will fix it soon";

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Incorrect code entered";
                        }
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
