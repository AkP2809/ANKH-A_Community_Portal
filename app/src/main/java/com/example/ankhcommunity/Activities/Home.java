package com.example.ankhcommunity.Activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ankhcommunity.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Dialog popupAddPost;

    ImageView popupUserProfilePhoto, popupComplainPhoto, popupPostComplainBtn;
    EditText popupComplainTitle, popupComplainDescription;
    ProgressBar popupProgressBar;

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
            }
        });
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
}