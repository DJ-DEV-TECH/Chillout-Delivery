package com.app.chillout_delivery.bottomsheetdialog;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.adapter.OrderListAdapter;
import com.app.chillout_delivery.databinding.FragmentOrderDetailsListBinding;
import com.app.chillout_delivery.model.OrderModel;
import com.app.chillout_delivery.model.OrderResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsListFragment extends BottomSheetDialogFragment {

    public final static String TAG = OrderDetailsListFragment.class.getSimpleName();
    private FragmentOrderDetailsListBinding binding;
    private double totalAmount;
    private List<OrderModel.Items> orderList = new ArrayList<>();

    public static OrderDetailsListFragment newInstance(List<OrderResponse> orderModelList) {
        OrderDetailsListFragment fragment = new OrderDetailsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailsListBinding.inflate(getLayoutInflater());
        setupOrderRecycler(orderList);
        binding.closeImg.setOnClickListener(v -> {
            dismiss();
        });
        return binding.getRoot();
    }

    public void setData(double totalAmount, List<OrderModel.Items> orderModelList) {
        this.totalAmount = totalAmount;
        this.orderList = orderModelList;
    }

    // ✅ CATEGORY LIST
    private void setupOrderRecycler(List<OrderModel.Items> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.orderListRecycler.setLayoutManager(layoutManager);
        // ✅ Divider line add
        binding.orderListRecycler.addItemDecoration(
                new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        binding.orderListRecycler.setAdapter(new OrderListAdapter(list, getContext()));
        binding.totalItemsTxt.setText("Total Items ("+list.size()+")");
        binding.totalItemsAmountTxt.setText("₹"+totalAmount);
        binding.totalTxt.setText("₹"+totalAmount);
    }
}