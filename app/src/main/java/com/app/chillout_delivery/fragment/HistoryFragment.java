package com.app.chillout_delivery.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.adapter.HistoryAdapter;
import com.app.chillout_delivery.adapter.OrdersAdapter;
import com.app.chillout_delivery.adapter.ViewPagerAdapter;
import com.app.chillout_delivery.base.BaseFragment;
import com.app.chillout_delivery.bottomsheetdialog.OrderDetailsListFragment;
import com.app.chillout_delivery.databinding.FragmentHistoryBinding;
import com.app.chillout_delivery.listener.OrderListener;
import com.app.chillout_delivery.model.OrderModel;
import com.app.chillout_delivery.model.OrderPageResponse;
import com.app.chillout_delivery.model.OrderResponse;
import com.app.chillout_delivery.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends BaseFragment implements OrderListener {

    private FragmentHistoryBinding binding;
//    ViewPagerAdapter adapter;
//    String[] tabTitles = {"Pending", "Accepted", "Completed"};
    private HistoryAdapter adapter;
    private List<OrderResponse> orderList = new ArrayList<>();
    private int page = 0;
    private int size = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(getLayoutInflater());
//        adapter = new ViewPagerAdapter(getActivity());
//        binding.viewPager.setAdapter(adapter);

        /*new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitles[position]);
            }
        }).attach();*/

        // shimmer start
        binding.itemsShimmerLayout.startShimmer();

        setupRecycler();
        loadOrders();

        return binding.getRoot();
    }

    private void setupRecycler() {
        adapter = new HistoryAdapter(getActivity(), orderList, this);
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
    public void onOrderClick(OrderResponse orderModel) {
        if (!orderDetailsListFragment.isAdded()) {
            orderDetailsListFragment.setData(orderModel.getTotalAmount(), orderModel.getItems());
            orderDetailsListFragment.show(getChildFragmentManager(), OrderDetailsListFragment.TAG);
        }
    }
}