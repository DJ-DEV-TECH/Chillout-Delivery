package com.app.chillout_delivery.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.listener.OrderListener;
import com.app.chillout_delivery.model.OrderResponse;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    List<OrderResponse> list;
    OrderListener listener;

    public HistoryAdapter(Context context, List<OrderResponse> list, OrderListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void addData(List<OrderResponse> newList) {
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderResponse model = list.get(position);

        holder.orderTxt.setText("Order #" + model.getOrderId());
        holder.userNameTxt.setText(model.getUserDetails().getName());
        holder.addressTxt.setText(model.getUserDetails().getAddress().getFullAddress());
        holder.statusTxt.setText(model.getOrderStatus());

        Glide.with(holder.itemView.getContext())
                .load(model.getUserDetails().getImage())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.userImg);

        // Status Color
        switch (model.getOrderStatus()) {
            case "Pending":
            case "PENDING":
                holder.statusTxt.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.orange)));
                break;
            case "Delivered":
            case "DELIVERED":
                holder.statusTxt.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.green)));
                break;
            case "Cancelled":
            case "CANCELLED":
                holder.statusTxt.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.red)));
                break;
        }

        holder.viewDetailsTxt.setOnClickListener(v -> {
            listener.onOrderClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView orderTxt, userNameTxt, addressTxt;
        AppCompatTextView statusTxt, viewDetailsTxt;
        CircleImageView userImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTxt = itemView.findViewById(R.id.orderTxt);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            addressTxt = itemView.findViewById(R.id.addressTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
            viewDetailsTxt = itemView.findViewById(R.id.viewDetailsTxt);
            userImg = itemView.findViewById(R.id.userImg);
        }
    }
}