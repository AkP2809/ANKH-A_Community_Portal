package com.example.ankhcommunity.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ankhcommunity.Models.PostModel;
import com.example.ankhcommunity.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<PostModel> mData;

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
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgUserProfile);
    }

    @Override
    public int getItemCount() {
        return (mData.isEmpty() || mData == null ? 0 :  mData.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCategory;
        ImageView imgPost, imgUserProfile;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_complain_title);
            tvCategory = itemView.findViewById(R.id.row_complain_category);

            imgPost = itemView.findViewById(R.id.row_complain_image);
            imgUserProfile = itemView.findViewById(R.id.row_user_profile_photo);
        }

    }
}
