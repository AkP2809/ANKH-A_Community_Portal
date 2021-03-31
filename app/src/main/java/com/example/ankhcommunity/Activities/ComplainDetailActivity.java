package com.example.ankhcommunity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ankhcommunity.Models.CommentModel;
import com.example.ankhcommunity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class ComplainDetailActivity extends AppCompatActivity {

    ImageView complainPhoto, complainUserPhoto, complainCommentCurrentUserPhoto, complainCommentPostBtn;
    EditText complainCommentDescription;
    TextView complainTitle, complainCategory, complainDescription, complainDateName;

    String postKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_details);

        //setting Status Bar as transparent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        complainTitle = findViewById(R.id.complain_detail_title);
        complainCategory = findViewById(R.id.complain_detail_category);
        complainDescription = findViewById(R.id.complain_detail_description);
        complainDateName = findViewById(R.id.complain_detail_timestamp_name);

        complainCommentDescription = findViewById(R.id.complain_detail_comment_description);

        complainPhoto = findViewById(R.id.complain_detail_photo);
        complainUserPhoto = findViewById(R.id.complain_detail_user_profile_photo);
        complainCommentCurrentUserPhoto = findViewById(R.id.complain_detail_current_user_comment_photo);
        complainCommentPostBtn = findViewById(R.id.complain_detail_post_comment_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //adding post comment button click listener
        complainCommentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complainCommentPostBtn.setVisibility(View.INVISIBLE);

                DatabaseReference commentReference = firebaseDatabase.getReference("Comments").child(postKey).push();

                String commentDescription = complainCommentDescription.getText().toString();
                String userID = currentUser.getUid();
                String userName = currentUser.getDisplayName();
                String userPhoto = currentUser.getPhotoUrl().toString();

                CommentModel commentModel = new CommentModel(commentDescription, userID, userPhoto, userName);

                commentReference.setValue(commentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Comment posted.");
                        complainCommentDescription.setText("");
                        complainCommentPostBtn.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Couldn't post comment! " + e.getMessage());
                    }
                });
            }
        });

        //binding all the above data into views
        //first we need to get complain data, for that, we need to send the detail data to PostAdapter

        //now fetching the complain data into views
        String complainImage = getIntent().getExtras().getString("complainPicture");
        Glide.with(this).load(complainImage).into(complainPhoto);

        String cTitle = getIntent().getExtras().getString("complainTitle");
        complainTitle.setText(cTitle);

        String cCategory = getIntent().getExtras().getString("complainCategory");
        complainCategory.setText(cCategory);

        //timestamp

        String uImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(uImage).into(complainUserPhoto);

        String cDescription = getIntent().getExtras().getString("complainDescription");
        complainDescription.setText(cDescription);

        //comment user image
        Glide.with(this).load(currentUser.getPhotoUrl()).into(complainCommentCurrentUserPhoto);

        //get complain ID
        postKey = getIntent().getExtras().getString("postKey");

        String cDate = timeStampToString(getIntent().getExtras().getLong("complainDate"));
        complainDateName.setText(cDate);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);

        String date = DateFormat.format("EEEE, MMMM d, yyyy", calendar).toString();
        return date;
    }
}