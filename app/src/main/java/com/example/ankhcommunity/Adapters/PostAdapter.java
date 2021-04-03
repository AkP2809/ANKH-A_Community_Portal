package com.example.ankhcommunity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ankhcommunity.Activities.ComplainDetailActivity;
import com.example.ankhcommunity.Activities.ui.home.HomeFragment;
import com.example.ankhcommunity.Models.PostModel;
import com.example.ankhcommunity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<PostModel> mData;
    Boolean upvoteChecker = false;
    DatabaseReference upvoteReference = FirebaseDatabase.getInstance().getReference("Upvotes");

    public PostAdapter(Context mContext, List<PostModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_complain, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getComplainTitle());
        holder.tvCategory.setText(mData.get(position).getComplainCategory());

        Glide.with(mContext).load(mData.get(position).getComplainPicture()).into(holder.imgPost);

        String userPhoto = mData.get(position).getUserPhoto();
        if(userPhoto != null) {
            Glide.with(mContext).load(userPhoto).into(holder.imgUserProfile);
        } else {
            Glide.with(mContext).load(R.drawable.defaultuser).into(holder.imgUserProfile);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = currentUser.getUid();
        final String postKey = mData.get(position).getPostKey();

        holder.setUpvoteButtonStatus(postKey);
        holder.upvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upvoteChecker = true;

                upvoteReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if( upvoteChecker.equals(true) ) {
                            if( snapshot.child(postKey).hasChild(currentUserID) ) {
                                upvoteReference.child(postKey).child(currentUserID).removeValue();
                                upvoteChecker = false;
                            } else {
                                upvoteReference.child(postKey).child(currentUserID).setValue(true);
                                upvoteChecker = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mData.isEmpty() || mData == null ? 0 :  mData.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCategory, tvUpvoteCountView;
        ImageView imgPost, imgUserProfile, upvoteBtn;

        int upvotesCount;
        DatabaseReference upvotesRef;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_complain_title);
            tvCategory = itemView.findViewById(R.id.row_complain_category);

            imgPost = itemView.findViewById(R.id.row_complain_image);
            imgUserProfile = itemView.findViewById(R.id.row_user_profile_photo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent complainDetailActivity = new Intent(mContext, ComplainDetailActivity.class);
                    int position = getAdapterPosition();

                    complainDetailActivity.putExtra("complainTitle",mData.get(position).getComplainTitle());
                    complainDetailActivity.putExtra("complainCategory",mData.get(position).getComplainCategory());
                    complainDetailActivity.putExtra("complainDescription",mData.get(position).getComplainDescription());
                    complainDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    complainDetailActivity.putExtra("complainPicture",mData.get(position).getComplainPicture());
                    complainDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());

                    //complainDetailActivity.putExtra("userName",mData.get(position).getUserName());

                    long timeStamp = (long) mData.get(position).getTimeStamp();
                    complainDetailActivity.putExtra("complainDate",timeStamp);

                    mContext.startActivity(complainDetailActivity);
                }
            });
        }

        public void setUpvoteButtonStatus(final String postKey) {
            tvUpvoteCountView = itemView.findViewById(R.id.upVoteCountView);
            upvoteBtn = itemView.findViewById(R.id.upvoteBtn);

            upvotesRef = FirebaseDatabase.getInstance().getReference("Upvotes");

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String cUID = currentUser.getUid();
            //String upvotes = "Upvotes";

            upvotesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if( snapshot.child(postKey).hasChild(cUID) ) {
                        upvotesCount = (int)snapshot.child(postKey).getChildrenCount();

                        upvoteBtn.setImageResource(R.drawable.ic_like);
                        tvUpvoteCountView.setText(Integer.toString(upvotesCount));
                    } else {
                        upvotesCount = (int)snapshot.child(postKey).getChildrenCount();

                        upvoteBtn.setImageResource(R.drawable.ic_fav_shadow_24dp);
                        tvUpvoteCountView.setText(Integer.toString(upvotesCount));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
