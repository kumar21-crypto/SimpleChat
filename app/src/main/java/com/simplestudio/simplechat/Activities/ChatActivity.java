package com.simplestudio.simplechat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simplestudio.simplechat.Adaptors.MessageAdaptor;
import com.simplestudio.simplechat.Models.Message;
import com.simplestudio.simplechat.R;
import com.simplestudio.simplechat.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private String imageUri,receiverUID;
    private String userName;
    ActivityChatBinding binding;
    private ArrayList<Message> messages;
    private MessageAdaptor messageAdaptor;
    private String senderUID;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    String senderRoom,receiverRoom;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        messages = new ArrayList<>();
        messageAdaptor = new MessageAdaptor(this,messages);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        senderUID = auth.getUid();

        imageUri = getIntent().getStringExtra("image");
        userName = getIntent().getStringExtra("name");
        receiverUID = getIntent().getStringExtra("uid");

        senderRoom = senderUID + receiverUID;
        receiverRoom = receiverUID + senderUID;

        // setting adaptor for messages
        binding.chatActivityRecyclerView.setAdapter(messageAdaptor);
        binding.chatActivityRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        // setting profile image and name
        settingImageAndName();

        // setting status
        settingStatus();

        // fetching messages
        fetchMessages();




        binding.chatActivityArrowBack.setOnClickListener(v -> {
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
       });

        binding.chatActivitySend.setOnClickListener(v -> {

            String messageTxt = binding.chatActivityMessageBox.getText().toString();
            Date date = new Date();
            Message message = new Message(messageTxt, senderUID, date.getTime());

            database.getReference().child("chats").child(senderRoom)
                    .child("messages")
                    .push()
                    .setValue(message)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(ChatActivity.this, "message sent successfully", Toast.LENGTH_SHORT).show();

                            database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .push()
                                    .setValue(message)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ChatActivity.this, "message receive successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

            binding.chatActivityMessageBox.getText().clear();





        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentID = auth.getUid();
        if (currentID != null)
        {
            database.getReference().child("connection").child(currentID).setValue("online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String currentID = auth.getUid();
        if (currentID != null)
        {
            database.getReference().child("connection").child(currentID).setValue("offline");
        }
    }

    private void settingStatus() {
        database.getReference().child("connection").child(receiverUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String status = snapshot.getValue(String.class);

                    if (!status.isEmpty())
                    {
                        binding.chatActivityUserStatus.setText(status);
                        binding.chatActivityUserStatus.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchMessages() {

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messages.clear();

                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            messages.add(message);
                        }
                        messageAdaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void settingImageAndName() {

        Glide.with(ChatActivity.this).load(imageUri).placeholder(R.drawable.user)
                .into(binding.chatActivityImageView);

        binding.chatActivityUserName.setText(userName);
    }


}