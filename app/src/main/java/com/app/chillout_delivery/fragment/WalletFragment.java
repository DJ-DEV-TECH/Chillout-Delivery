package com.app.chillout_delivery.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.databinding.FragmentWalletBinding;

public class WalletFragment extends Fragment {

    private FragmentWalletBinding binding;

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}