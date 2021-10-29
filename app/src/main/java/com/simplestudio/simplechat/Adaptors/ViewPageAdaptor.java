package com.simplestudio.simplechat.Adaptors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.simplestudio.simplechat.Fragments.CallsFragment;
import com.simplestudio.simplechat.Fragments.ChatFragment;
import com.simplestudio.simplechat.Fragments.GroupFragment;

public class ViewPageAdaptor extends FragmentStateAdapter {

    private String titles[] = new String[]{"Chats","Groups","Calls"};

    public ViewPageAdaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new ChatFragment();

            case 1:
                return new GroupFragment();

            case 2:
                return new CallsFragment();
        }

        return new ChatFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
