package com.simplestudio.simplechat.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.simplestudio.simplechat.Activities.MainActivity;
import com.simplestudio.simplechat.Models.Status;
import com.simplestudio.simplechat.Models.UserStatus;
import com.simplestudio.simplechat.R;
import com.simplestudio.simplechat.databinding.StatusLayoutBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdaptor extends RecyclerView.Adapter<StatusAdaptor.StatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    public StatusAdaptor(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.status_layout, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);

        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size()-1);

        Glide.with(context).load(lastStatus.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.binding.circularImageViewStatus);

        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());

        holder.binding.circularStatusView.setOnClickListener(v -> {

            ArrayList<MyStory> myStories = new ArrayList<>();

            for (Status status : userStatus.getStatuses())
            {
                myStories.add(new MyStory(status.getImageUrl()));
            }

            new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                    .setStoriesList(myStories)
                    .setStoryDuration(5000)
                    .setTitleText(userStatus.getName())
                    .setSubtitleText("hello")
                    .setTitleLogoUrl(userStatus.getProfileImage())
                    .setStoryClickListeners(new StoryClickListeners() {
                        @Override
                        public void onDescriptionClickListener(int i) {

                        }

                        @Override
                        public void onTitleIconClickListener(int i) {

                        }
                    }).build().show();

        });
    }

    @Override
    public int getItemCount() {
        return userStatuses == null ? 0 : userStatuses.size();
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder {

        StatusLayoutBinding binding;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StatusLayoutBinding.bind(itemView);
        }
    }

}
