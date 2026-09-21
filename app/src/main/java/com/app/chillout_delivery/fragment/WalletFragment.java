package com.app.chillout_delivery.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.chillout_delivery.adapter.HistoryAdapter;
import com.app.chillout_delivery.base.BaseFragment;
import com.app.chillout_delivery.bottomsheetdialog.OrderDetailsListFragment;
import com.app.chillout_delivery.databinding.FragmentWalletBinding;
import com.app.chillout_delivery.listener.OrderListener;
import com.app.chillout_delivery.model.DeliveryDashboard;
import com.app.chillout_delivery.model.OrderPageResponse;
import com.app.chillout_delivery.model.OrderResponse;
import com.app.chillout_delivery.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletFragment extends BaseFragment implements OrderListener {

    private FragmentWalletBinding binding;
    private HistoryAdapter adapter;
    private List<OrderResponse> orderList = new ArrayList<>();
    private int page = 0;
    private int size = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(getLayoutInflater());

        // shimmer start
        binding.itemsShimmerLayout.startShimmer();

        setupRecycler();
        loadDashboard();
        loadOrders();

        return binding.getRoot();
    }

    private void setupRecycler() {
        adapter = new HistoryAdapter(getActivity(), orderList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recentOrdersRecycler.setLayoutManager(layoutManager);
        binding.recentOrdersRecycler.setAdapter(adapter);
        binding.recentOrdersRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                            loadOrders();
                        }
                    }
                }
            }
        });
    }

    private void loadDashboard() {
        apiService.getDashboard(Utils.getAuthToken(authToken)).enqueue(new Callback<DeliveryDashboard>() {
            @Override
            public void onResponse(Call<DeliveryDashboard> call, Response<DeliveryDashboard> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeliveryDashboard dashboard = response.body();
                    binding.txtTotalOrders.setText(""+dashboard.getTotalOrders());
                    binding.txtTotalDelivery.setText(""+dashboard.getTotalDelivered());
                    binding.txtTotalCancelledOrders.setText(""+dashboard.getTotalCancelled());
                }
            }

            @Override
            public void onFailure(Call<DeliveryDashboard> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadOrders() {
        isLoading = true;
        apiService.getOrders(Utils.getAuthToken(authToken), page, size).enqueue(new Callback<OrderPageResponse>() {
            @Override
            public void onResponse(Call<OrderPageResponse> call, Response<OrderPageResponse> response) {
                isLoading = false;
                binding.itemsShimmerLayout.stopShimmer();
                binding.itemsShimmerLayout.setVisibility(View.GONE);
                binding.recentOrdersRecycler.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderResponse> newData = response.body().getData();
//                    orderList.addAll(newData);
                    adapter.addData(newData);
                    if (page >= response.body().getTotalPages() - 1) {
                        isLastPage = true;
                    } else {
                        page++;
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderPageResponse> call, Throwable t) {
                isLoading = false;
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onOrderClick(OrderResponse orderModel) {
        if (!orderDetailsListFragment.isAdded()) {
            orderDetailsListFragment.setData(orderModel.getTotalAmount(), orderModel.getItems());
            orderDetailsListFragment.show(getChildFragmentManager(), OrderDetailsListFragment.TAG);
        }
    }
}