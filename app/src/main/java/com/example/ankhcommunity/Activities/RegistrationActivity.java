package com.example.ankhcommunity.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ankhcommunity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationActivity extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;

    private EditText userName, userEmail, userPassword, userConfirmPassword;
    private ProgressBar loadingProgress;
    private Button registrationBtn, anonymousLBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        //anonymous login
        anonymousLBtn = findViewById(R.id.anonymousLoginBtn);
        anonymousLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            showMessage("Registration Completed Successfully");
                            updateUI();
                        }
                    }
                });
            }
        });

        userName = findViewById(R.id.regName);
        userEmail = findViewById(R.id.loginEmail);
        userPassword = findViewById(R.id.loginPassword);
        userConfirmPassword = findViewById(R.id.regConfirmPassword);
        loadingProgress = findViewById(R.id.progressBar);
        registrationBtn = findViewById(R.id.regBtn);

        loadingProgress.setVisibility(View.INVISIBLE);

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String uName = userName.getText().toString();
                final String uEmail = userEmail.getText().toString();
                final String uPassword = userPassword.getText().toString();
                final String uConfirmPassword = userConfirmPassword.getText().toString();

                if( uName.isEmpty() || uEmail.isEmpty() || uPassword.isEmpty() || uConfirmPassword.isEmpty() ) {
                        showMessage("Please fill all the fields.");
                        registrationBtn.setVisibility(View.VISIBLE);
                        loadingProgress.setVisibility(View.INVISIBLE);
                }
                if( !uPassword.equals(uConfirmPassword) ) {
                    showMessage("Passwords don't match in both the fields.");
                    registrationBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else {
                    createUserAccount(uName, uEmail, uPassword);
                }
            }
        });

        ImgUserPhoto = findViewById(R.id.loginPhoto);

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });
    }

    //method to create user with a valid email
    private void createUserAccount(String uName, String uEmail, String uPassword) {
        mAuth.createUserWithEmailAndPassword(uEmail, uPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ) {
                    //user account successfully created
                    showMessage("Account created Successfully!");

                    //after creation of user account, profile photo and name updation is needed
                    updateUserDetails(uName, pickedImgUri, mAuth.getCurrentUser());
                } else {
                    //user account creation failed
                    showMessage("Account creation failed. Please try again!" + task.getException().getMessage());

                    registrationBtn.setVisibility(View.VISIBLE);
                    registrationBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //update user name and profile photo
    private void updateUserDetails(String uName, Uri pickedImgUri, FirebaseUser currentUser) {
        //uploading profile photo to firebase storage and retrieving the URL
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_profile_photos");
        StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded successfully, now retrieving image URL
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //uri contains image URL
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uName)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        //user profile successfully updated
                                        showMessage("Registration Completed Successfully");
                                        updateUI();
                                    }
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent homePageActivity = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(homePageActivity);
        finish();
    }

    //method to show some toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        //open gallery intent and wait until user selects an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegistrationActivity.this, "Please grant the required permission.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            //user has picked an image and now, its reference is to be stored to a Uri variable

            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }
}