package com.app.chillout_delivery.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.adapter.ViewPagerAdapter;
import com.app.chillout_delivery.databinding.FragmentHistoryBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    ViewPagerAdapter adapter;
    String[] tabTitles = {"Pending", "Accepted", "Completed"};

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(getLayoutInflater());
        adapter = new ViewPagerAdapter(getActivity());
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitles[position]);
            }
        }).attach();
        return binding.getRoot();
    }
}