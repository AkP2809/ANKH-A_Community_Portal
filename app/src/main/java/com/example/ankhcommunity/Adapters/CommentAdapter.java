package com.example.ankhcommunity.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ankhcommunity.Models.CommentModel;
import com.example.ankhcommunity.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<CommentModel> mData;

    public CommentAdapter(Context mContext, List<CommentModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);
        return new CommentAdapter.CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgCommentUser);

        holder.tvCommentUserName.setText(mData.get(position).getUserName());
        holder.tvCommentDescription.setText(mData.get(position).getContent());

        holder.tvCommentTime.setText(timeStampToString((Long)mData.get(position).getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCommentUser;
        TextView tvCommentUserName, tvCommentDescription, tvCommentTime;

        public CommentViewHolder(View itemView) {
            super(itemView);

            imgCommentUser = itemView.findViewById(R.id.comment_user_image);

            tvCommentUserName = itemView.findViewById(R.id.comment_user_name);
            tvCommentDescription = itemView.findViewById(R.id.comment_description);
            tvCommentTime = itemView.findViewById(R.id.comment_time);
        }
    }

    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);

        String date = DateFormat.format("hh:mm", calendar).toString();
        return date;
    }
}
