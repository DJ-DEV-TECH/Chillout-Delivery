package com.app.chillout_delivery.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.activity.OrderDetailsActivity;
import com.app.chillout_delivery.adapter.OrdersAdapter;
import com.app.chillout_delivery.base.BaseFragment;
import com.app.chillout_delivery.databinding.FragmentHomeBinding;
import com.app.chillout_delivery.listener.OrderStatusListener;
import com.app.chillout_delivery.model.OrderPageResponse;
import com.app.chillout_delivery.model.OrderResponse;
import com.app.chillout_delivery.utils.EventManager;
import com.app.chillout_delivery.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements OrderStatusListener {

    private FragmentHomeBinding binding;
    private OrdersAdapter adapter;
    private List<OrderResponse> orderList = new ArrayList<>();
    private int page = 0;
    private int size = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        // shimmer start
        binding.itemsShimmerLayout.startShimmer();

        binding.swipeRefresh.setOnRefreshListener(() -> {
            page = 0;
            orderList.clear();
            adapter.notifyDataSetChanged();
            loadOrders();
        });

        getOrderEvent();
        setupRecycler();
        loadOrders();

        return binding.getRoot();
    }

    private void setupRecycler() {
        adapter = new OrdersAdapter(getActivity(), orderList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.orderRecycler.setLayoutManager(layoutManager);
        binding.orderRecycler.setAdapter(adapter);
        binding.orderRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void loadOrders() {
        isLoading = true;
        apiService.getOrders(Utils.getAuthToken(authToken), page, size).enqueue(new Callback<OrderPageResponse>() {
            @Override
            public void onResponse(Call<OrderPageResponse> call, Response<OrderPageResponse> response) {
                isLoading = false;
                binding.swipeRefresh.setRefreshing(false);
                binding.itemsShimmerLayout.stopShimmer();
                binding.itemsShimmerLayout.setVisibility(View.GONE);
                binding.orderRecycler.setVisibility(View.VISIBLE);
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
    public void onOrderStatusUpdate(String type, OrderResponse orderResponse) {

    }

    @Override
    public void onOrderTrack(OrderResponse orderResponse) {
        Intent i = new Intent(requireContext(), OrderDetailsActivity.class);
        i.putExtra("orderId", orderResponse.getOrderId());
        startActivity(i);
    }

    private void getOrderEvent() {
        EventManager.getInstance().getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            switch (event.type) {
                case "ORDER_UPDATE":
                    OrderResponse orderResponse = new Gson().fromJson(event.data, OrderResponse.class);
                    break;
            }
        });
    }
}