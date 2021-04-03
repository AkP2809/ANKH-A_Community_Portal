package com.example.ankhcommunity.Activities.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ankhcommunity.Adapters.PostAdapter;
import com.example.ankhcommunity.Models.PostModel;
import com.example.ankhcommunity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    //private HomeViewModel homeViewModel;

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, upvoteReference;
    List<PostModel> complaintList = new ArrayList<PostModel>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        postRecyclerView = fragmentView.findViewById(R.id.complainRV);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        llm.setReverseLayout(true);
        postRecyclerView.setLayoutManager(llm);
        postRecyclerView.setHasFixedSize(true);

        /*postAdapter = new PostAdapter(getActivity(), complaintList);
        postRecyclerView.setAdapter(postAdapter);*/

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");
        upvoteReference = firebaseDatabase.getReference("Upvotes");

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //get the list of complaints from the firebase database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                complaintList = new ArrayList<PostModel>();
                for( DataSnapshot complainSnap : snapshot.getChildren() ) {
                    PostModel post = complainSnap.getValue(PostModel.class);
                    complaintList.add(post);

                    //DatabaseReference postRef =  firebaseDatabase.getReference("Comments").child(post.getPostKey());
                }

                postAdapter = new PostAdapter(getActivity(), complaintList);
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}