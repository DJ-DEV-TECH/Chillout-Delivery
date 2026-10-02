package com.app.chillout_delivery.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.app.chillout_delivery.utils.EventManager;
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

        getOrderEvent();

        binding.nameTxt.setText(name);
        binding.fullNameTxt.setText(name);
        binding.profileMbleTxt.setText(mobile);
        binding.mbleNumberTxt.setText(mobile);
        if (status == 0 || status == 1) {
            binding.statusLinear.setVisibility(VISIBLE);
            binding.statusTxt.setText((status == 0) ? "OFFLINE" : "ONLINE");
            binding.deliveryLottie.setVisibility(GONE);
            binding.deliveryLottie.pauseAnimation();
        } else {
            binding.statusLinear.setVisibility(GONE);
            binding.deliveryLottie.setVisibility(VISIBLE);
            binding.deliveryLottie.playAnimation();
        }
        binding.statusTxt.setTextColor((status == 0) ? requireContext().getColor(R.color.red) : requireContext().getColor(R.color.green));
        binding.onlineImg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(),
                                status == 0 ? R.color.red : R.color.green)));
        binding.emailTxt.setText(email);

        binding.logoutBtn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
            requireActivity().finish();
            PrefsHelper.clearAll(getContext());
        });

        binding.editLinear.setOnClickListener(v -> {

        });

        binding.helpConstraint.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebviewActivity.class);
            intent.putExtra("url", "https://www.google.com");
            startActivity(intent);
        });

        binding.privacyConstraint.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebviewActivity.class);
            intent.putExtra("url", "https://www.google.com");
            startActivity(intent);
        });

        return binding.getRoot();
    }

    private void getOrderEvent() {
        EventManager.getInstance().getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            if (event.type.equals("USER_STATUS")) {
                String activeStatus = event.status;
                if (activeStatus.equals("0") || activeStatus.equals("1")) {
                    binding.statusLinear.setVisibility(VISIBLE);
                    binding.deliveryLottie.setVisibility(GONE);
                    binding.statusTxt.setText(activeStatus.equalsIgnoreCase("0") ? "OFFLINE" : "ONLINE");
                    binding.statusTxt.setTextColor(activeStatus.equalsIgnoreCase("0") ? requireContext().getColor(R.color.red) : requireContext().getColor(R.color.green));
                    binding.onlineImg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(),
                            activeStatus.equalsIgnoreCase("0") ? R.color.red : R.color.green)));
                    binding.deliveryLottie.pauseAnimation();
                } else {
                    binding.statusLinear.setVisibility(GONE);
                    binding.deliveryLottie.setVisibility(VISIBLE);
                    binding.deliveryLottie.playAnimation();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}