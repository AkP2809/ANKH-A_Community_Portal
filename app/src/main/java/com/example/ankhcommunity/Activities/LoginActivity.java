package com.example.ankhcommunity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ankhcommunity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    private Button loginBtn, anonymousLoginBtn;
    private ProgressBar loginProgress, anonymousLoginProgress;
    private FirebaseAuth mAuth;
    private Intent HomePageActivity;
    private TextView regRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        userEmail = findViewById(R.id.loginEmail);
        userPassword = findViewById(R.id.loginPassword);

        loginProgress = findViewById(R.id.loginProgressBar);
        loginProgress.setVisibility(View.INVISIBLE);

        anonymousLoginProgress = findViewById(R.id.anonymousProgressBar);
        anonymousLoginProgress.setVisibility(View.INVISIBLE);

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setVisibility(View.INVISIBLE);
                loginProgress.setVisibility(View.VISIBLE);

                final String uEmail = userEmail.getText().toString();
                final String uPassword = userPassword.getText().toString();

                if( uEmail.isEmpty() || uPassword.isEmpty() ) {
                    showMessage("Please fill both the fields.");

                    loginBtn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else {
                    userSignIn(uEmail, uPassword);
                }
            }
        });

        regRedirect = findViewById(R.id.regRedirectText);
        regRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });

        //anonymous login
        anonymousLoginBtn = findViewById(R.id.anonymousLoginBtn);
        anonymousLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonymousLoginBtn.setVisibility(View.INVISIBLE);
                anonymousLoginProgress.setVisibility(View.VISIBLE);

                if(currentUser == null) {
                    mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                showMessage("Signed-in anonymously!");
                                updateUI();
                            }
                        }
                    });
                } else {
                    showMessage("Something went wrong. Please try again later");
                    anonymousLoginBtn.setVisibility(View.VISIBLE);
                    anonymousLoginProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    //user sign-in method
    private void userSignIn(String uEmail, String uPassword) {
        mAuth.signInWithEmailAndPassword(uEmail, uPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //login successful
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);

                    showMessage("Login Successful!");
                    updateUI();
                } else {
                    showMessage(task.getException().getMessage());

                    loginBtn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //redirect to home page
    private void updateUI() {
        HomePageActivity = new Intent(this, com.example.ankhcommunity.Activities.Home.class);
        startActivity(HomePageActivity);
        finish();
    }

    //simple method to display toast
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            //user is already logged-in, so user gets redirected to home page directly
            updateUI();
        }
    }
}