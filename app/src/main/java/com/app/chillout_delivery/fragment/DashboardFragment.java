package com.app.chillout_delivery.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.base.BaseFragment;
import com.app.chillout_delivery.databinding.FragmentDashboardBinding;
import com.app.chillout_delivery.model.DeliveryDashboard;
import com.app.chillout_delivery.model.OrderPageResponse;
import com.app.chillout_delivery.model.OrderResponse;
import com.app.chillout_delivery.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends BaseFragment {

    private FragmentDashboardBinding binding;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    private void loadDashboard() {
        apiService.getDashboard(Utils.getAuthToken(authToken)).enqueue(new Callback<DeliveryDashboard>() {
            @Override
            public void onResponse(Call<DeliveryDashboard> call, Response<DeliveryDashboard> response) {
                if (response.isSuccessful() && response.body() != null) {

                }
            }

            @Override
            public void onFailure(Call<DeliveryDashboard> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}