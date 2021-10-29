package com.simplestudio.simplechat.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.simplestudio.simplechat.Activities.ChatActivity;
import com.simplestudio.simplechat.R;
import com.simplestudio.simplechat.Models.User;
import com.simplestudio.simplechat.databinding.UserConversationBinding;

import java.util.ArrayList;

public class UsersAdaptor extends RecyclerView.Adapter<UsersAdaptor.UserViewHolder>{


    Context context;
    ArrayList<User> users;


    public UsersAdaptor(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_conversation, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = users.get(position);

        holder.binding.userName.setText(user.getName());
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.user).into(holder.binding.conversationImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("image",user.getProfileImage());
                intent.putExtra("name",user.getName());
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);

            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            Toast.makeText(context, "long press", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        UserConversationBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = UserConversationBinding.bind(itemView);

        }
    }
}
