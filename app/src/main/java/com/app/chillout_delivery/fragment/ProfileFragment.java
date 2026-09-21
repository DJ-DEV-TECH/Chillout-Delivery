package com.app.chillout_delivery.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.activity.LoginActivity;
import com.app.chillout_delivery.activity.WebviewActivity;
import com.app.chillout_delivery.base.BaseFragment;
import com.app.chillout_delivery.databinding.FragmentProfileBinding;
import com.app.chillout_delivery.utils.PrefsHelper;

public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        binding.nameTxt.setText(name);
        binding.fullNameTxt.setText(name);
        binding.profileMbleTxt.setText(mobile);
        binding.mbleNumberTxt.setText(mobile);
        binding.statusTxt.setText((status == 0) ? "OFFLINE" : "ONLINE");
        binding.statusTxt.setTextColor((status == 0) ? getActivity().getColor(R.color.red) : getActivity().getColor(R.color.green));
        binding.onlineImg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),
                                status == 0 ? R.color.red : R.color.green)));
        binding.emailTxt.setText(email);

        binding.logoutBtn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
            requireActivity().finishAndRemoveTask();
            PrefsHelper.clearAll(getContext());
        });

        binding.editLinear.setOnClickListener(v -> {

        });

        binding.helpConstraint.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebviewActivity.class);
            intent.putExtra("url", "www.google.com");
            startActivity(intent);
        });

        binding.privacyConstraint.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebviewActivity.class);
            intent.putExtra("url", "www.google.com");
            startActivity(intent);
        });

        return binding.getRoot();
    }

    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer activeStatus = intent.getIntExtra("status", 0);
            binding.statusTxt.setText((activeStatus == 0) ? "OFFLINE" : "ONLINE");
            binding.statusTxt.setTextColor((activeStatus == 0) ? getActivity().getColor(R.color.red) : getActivity().getColor(R.color.green));
            binding.onlineImg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),
                    activeStatus == 0 ? R.color.red : R.color.green)));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(STATUS_EVENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getActivity().registerReceiver(statusReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            getActivity().registerReceiver(statusReceiver, filter);
        }
    }
}