package com.app.chillout_delivery.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.chillout_delivery.fragment.AcceptedOrderFragment;
import com.app.chillout_delivery.fragment.CompletedOrderFragment;
import com.app.chillout_delivery.fragment.PendingOrderFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PendingOrderFragment();
            case 1:
                return new AcceptedOrderFragment();
            case 2:
                return new CompletedOrderFragment();
            default:
                return new PendingOrderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // number of tabs
    }
}
