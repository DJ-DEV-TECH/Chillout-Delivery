package com.app.chillout_delivery.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.databinding.FragmentCompletedOrderBinding;

public class CompletedOrderFragment extends Fragment {

    private FragmentCompletedOrderBinding binding;

    public static CompletedOrderFragment newInstance() {
        CompletedOrderFragment fragment = new CompletedOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCompletedOrderBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}