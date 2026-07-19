package com.app.chillout_delivery.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.databinding.FragmentAcceptedOrderBinding;

public class AcceptedOrderFragment extends Fragment {

    private FragmentAcceptedOrderBinding binding;

    public static AcceptedOrderFragment newInstance() {
        AcceptedOrderFragment fragment = new AcceptedOrderFragment();
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
        binding = FragmentAcceptedOrderBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}