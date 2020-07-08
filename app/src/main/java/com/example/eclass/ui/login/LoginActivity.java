package com.example.eclass.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eclass.MainActivity;
import com.example.eclass.R;
import com.example.eclass.RegisterActivity;
import com.example.eclass.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText phone, password;
    private Button signinButton, registerButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        mDatabase = FirebaseDatabase.getInstance().getReference("Student");

        phone = findViewById(R.id.lgEditPhone);
        password = findViewById(R.id.lgEditPassword);
        signinButton = findViewById(R.id.lgSignIn);
        registerButton = findViewById(R.id.lgRegister);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                signinButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    phone.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    password.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        /*

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(phone.getText().toString(),
                        password.getText().toString());
            }
        };
        phone.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(phone.getText().toString(),
                            password.getText().toString());
                }
                return false;
            }
        });
        */

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
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