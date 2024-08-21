package com.ravemaster.spotifydownloader.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ravemaster.spotifydownloader.fragments.AlbumFragment;
import com.ravemaster.spotifydownloader.fragments.PlaylistFragment;
import com.ravemaster.spotifydownloader.fragments.SongFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new PlaylistFragment();
            case 2:
                return new AlbumFragment();
            default:
                return new SongFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
