package com.simplestudio.simplechat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simplestudio.simplechat.Models.User;
import com.simplestudio.simplechat.Adaptors.UsersAdaptor;
import com.simplestudio.simplechat.databinding.FragmentChatBinding;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {


    FragmentChatBinding binding;
    UsersAdaptor usersAdaptor;
    private FirebaseAuth auth ;
    private FirebaseDatabase database;
    private CircleImageView circleImageView;
     ArrayList<User> users;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater,container, false);

        users = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        usersAdaptor = new UsersAdaptor(getActivity(), users);

        binding.conversationRecyclerView.setAdapter(usersAdaptor);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    User user = snapshot1.getValue(User.class);
                    if (!FirebaseAuth.getInstance().getUid().equals(user.getUid()))
                    {
                        users.add(user);
                    }

                }
                usersAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return binding.getRoot();
    }
}