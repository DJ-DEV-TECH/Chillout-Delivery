package com.app.chillout_delivery.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.databinding.FragmentPendingOrderBinding;

public class PendingOrderFragment extends Fragment {

    private FragmentPendingOrderBinding binding;

    public static PendingOrderFragment newInstance() {
        PendingOrderFragment fragment = new PendingOrderFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPendingOrderBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }
}