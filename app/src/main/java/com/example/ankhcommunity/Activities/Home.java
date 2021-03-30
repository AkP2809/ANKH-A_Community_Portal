package com.example.ankhcommunity.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ankhcommunity.Activities.ui.home.HomeFragment;
import com.example.ankhcommunity.Models.PostModel;
import com.example.ankhcommunity.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Dialog popupAddPost;

    ImageView popupUserProfilePhoto, popupComplainPhoto, popupPostComplainBtn;
    EditText popupComplainTitle, popupComplainDescription;
    ProgressBar popupProgressBar;

    static int PReqCode = 2;
    static int REQUESTCODE = 2;
    Uri pickedImgUri = null;

    String complainCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //popup init
        initPopup();
        //init popup photo picking
        setupPopupPhotoUpload();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAddPost.show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_settings, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        updateNavHeader();

        //setting the home_fragment as the default one
        //getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_container, new HomeFragment()).commit();
    }

    private void setupPopupPhotoUpload() {
        popupComplainPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when this image view is clicked, user can upload an image from gallery
                //but before that, we need to check if the app has access to system files or not

                if(Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });
    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Home.this, "Please grant the required permission.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        //open gallery intent and wait until user selects an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            //user has picked an image and now, its reference is to be stored to a Uri variable

            pickedImgUri = data.getData();
            popupComplainPhoto.setImageURI(pickedImgUri);
        }
    }

    private void initPopup() {
        popupAddPost = new Dialog(this);
        popupAddPost.setContentView(R.layout.post_complain_popup);
        popupAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //spinner for dropdown menu
        Spinner spinner = (Spinner) popupAddPost.findViewById(R.id.popup_spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.complain_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //init popup widgets
        popupUserProfilePhoto = popupAddPost.findViewById(R.id.popup_user_photo);
        popupComplainPhoto = popupAddPost.findViewById(R.id.popup_complain_photo);
        popupPostComplainBtn = popupAddPost.findViewById(R.id.popup_complain_submit);

        popupComplainTitle = popupAddPost.findViewById(R.id.popup_complain_title);
        popupComplainDescription = popupAddPost.findViewById(R.id.popup_complain_description);

        popupProgressBar = popupAddPost.findViewById(R.id.popup_progressBar);

        //loading user profile photo
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserProfilePhoto);

        //Add post click listener
        popupPostComplainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupPostComplainBtn.setVisibility(View.INVISIBLE);
                popupProgressBar.setVisibility(View.VISIBLE);

                //checking if all fields are filled before uploading it to firebase
                if( !popupComplainTitle.getText().toString().isEmpty() &&
                        !complainCategory.isEmpty() &&
                        !popupComplainDescription.getText().toString().isEmpty() &&
                        pickedImgUri != null) {
                    //everything is proper, TODO: Create a POST object and add it to firebase storage(database)

                    //before uploading the POST object, upload of complainImage is needed
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("complain_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String complainImageDownloadLink = uri.toString();

                                    //now creating POST object

                                    PostModel post = new PostModel( popupComplainTitle.getText().toString(),
                                            complainCategory, popupComplainDescription.getText().toString(),
                                            complainImageDownloadLink, currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString());

                                    //add POST object to firebase
                                    addComplain(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //if picture doesn't get uploaded
                                    showMessage(e.getMessage());

                                    popupPostComplainBtn.setVisibility(View.VISIBLE);
                                    popupProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });

                } else {
                    //something is not properly filled/chosen
                    showMessage("Please verify that all fields are filled properly and a proper image is selected.");

                    popupPostComplainBtn.setVisibility(View.VISIBLE);
                    popupProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addComplain(PostModel post) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("Complaints").push();

        //get unique post_ID and update postKey
        String key = myRef.getKey();
        post.setPostKey(key);

        //add POST object data to firebase database
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Complain Posted Successfully!");

                popupPostComplainBtn.setVisibility(View.VISIBLE);
                popupProgressBar.setVisibility(View.INVISIBLE);

                popupAddPost.dismiss();
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        //using glide to load the user profile photo
        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        complainCategory = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), complainCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //stay in the popup dialog
    }
}