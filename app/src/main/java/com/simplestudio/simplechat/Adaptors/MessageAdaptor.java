package com.simplestudio.simplechat.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.simplestudio.simplechat.Activities.ChatActivity;
import com.simplestudio.simplechat.Models.Message;
import com.simplestudio.simplechat.R;
import com.simplestudio.simplechat.databinding.RecieverMessageLayoutBinding;
import com.simplestudio.simplechat.databinding.SenderMessageLayoutBinding;

import java.util.ArrayList;

public class MessageAdaptor extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    public MessageAdaptor(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message_layout, parent, false);
            return new SenderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciever_message_layout, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderID()))
        {
            return ITEM_SENT;
        }
        else
        {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         Message message = messages.get(position);

        if (holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder)holder;
            viewHolder.binding.chatActivitySenderMsg.setText(message.getMessage());
        }
        else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            viewHolder.binding.chatActivityReceiverMsg.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        SenderMessageLayoutBinding binding;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SenderMessageLayoutBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        RecieverMessageLayoutBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecieverMessageLayoutBinding.bind(itemView);
        }
    }
}
