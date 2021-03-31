package com.example.ankhcommunity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.LauncherApps;
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
import com.example.ankhcommunity.Adapters.CommentAdapter;
import com.example.ankhcommunity.Models.CommentModel;
import com.example.ankhcommunity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ComplainDetailActivity extends AppCompatActivity {

    ImageView complainPhoto, complainUserPhoto, complainCommentCurrentUserPhoto, complainCommentPostBtn;
    EditText complainCommentDescription;
    TextView complainTitle, complainCategory, complainDescription, complainDateName;

    String postKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;

    RecyclerView commentRecyclerView;
    List<CommentModel> commentList = new ArrayList<CommentModel>();
    CommentAdapter commentAdapter;

    static String COMMENT_KEY = "Comments";

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

        commentRecyclerView = findViewById(R.id.rv_comments);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        llm.setReverseLayout(true);
        commentRecyclerView.setLayoutManager(llm);
        commentRecyclerView.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //adding post comment button click listener
        complainCommentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complainCommentPostBtn.setVisibility(View.INVISIBLE);

                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(postKey).push();

                String commentDescription = complainCommentDescription.getText().toString();
                String userID = currentUser.getUid();
                String userName = currentUser.getDisplayName();
                String userPhoto = (currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null);

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

        String uImage = getIntent().getExtras().getString("userPhoto");
        if(uImage != null) {
            Glide.with(this).load(uImage).into(complainUserPhoto);
        } else {
            Glide.with(this).load(R.drawable.defaultuser).into(complainUserPhoto);
        }

        String cDescription = getIntent().getExtras().getString("complainDescription");
        complainDescription.setText(cDescription);

        //comment user image if it exists
        if(currentUser.getPhotoUrl() != null) {
            Glide.with(this).load(currentUser.getPhotoUrl()).into(complainCommentCurrentUserPhoto);
        } else {
            Glide.with(this).load(R.drawable.defaultuser).into(complainCommentCurrentUserPhoto);
        }

        //get complain ID
        postKey = getIntent().getExtras().getString("postKey");

        String cDate = timeStampToString(getIntent().getExtras().getLong("complainDate"));
        complainDateName.setText(cDate);

        //init recyclerview for comments
        initCommentRV();
    }

    private void initCommentRV() {
        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(postKey);

        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList = new ArrayList<CommentModel>();
                for(DataSnapshot snap : snapshot.getChildren()) {
                    CommentModel comment = snap.getValue(CommentModel.class);
                    commentList.add(comment);
                }

                commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                commentRecyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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