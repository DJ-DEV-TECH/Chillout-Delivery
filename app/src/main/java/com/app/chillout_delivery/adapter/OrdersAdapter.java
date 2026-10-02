package com.app.chillout_delivery.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.listener.OrderStatusListener;
import com.app.chillout_delivery.model.OrderResponse;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    Context context;
    List<OrderResponse> list;
    OrderStatusListener listener;

    public OrdersAdapter(Context context, List<OrderResponse> list, OrderStatusListener listener) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_order_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderResponse model = list.get(position);

        holder.orderTxt.setText("Order #" + model.getOrderId());
        holder.userNameTxt.setText(model.getUserDetails().getName());
        holder.addressTxt.setText(model.getUserDetails().getAddress().getFullAddress());
        holder.orderStatusTxt.setText(model.getOrderStatus());

        if (model.getUserDetails().getImage() != null) {
        Glide.with(holder.itemView.getContext())
                .load(model.getUserDetails().getImage())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.userImg);
        }

        // Status Color
        switch (model.getOrderStatus()) {
            case "Pending":
            case "PENDING":
                holder.trackBtn.setVisibility(GONE);
                holder.buttonLinear.setVisibility(VISIBLE);
                holder.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.orange)));
                break;
            case "ASSIGNED":
            case "ACCEPTED":
                holder.trackBtn.setVisibility(VISIBLE);
                holder.buttonLinear.setVisibility(GONE);
                holder.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow)));
                break;
            case "Completed":
            case "SUCCESS":
            case "DELIVERED":
                holder.trackBtn.setVisibility(GONE);
                holder.buttonLinear.setVisibility(GONE);
                holder.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.green)));
                break;
            case "Cancelled":
                holder.buttonLinear.setVisibility(GONE);
                holder.trackBtn.setVisibility(GONE);
                holder.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.red_1)));
                break;
            default:
                holder.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.red_1)));
                break;
        }

        // Accept Button
        holder.acceptBtn.setOnClickListener(v -> {
            listener.onOrderStatusUpdate("ACCEPT", model);
        });

        // Reject Button
        holder.rejectBtn.setOnClickListener(v -> {
            listener.onOrderStatusUpdate("REJECT", model);
        });

        // Track Button
        holder.trackBtn.setOnClickListener(v -> {
            listener.onOrderTrack(model);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView orderTxt, userNameTxt, addressTxt, durationTxt, orderStatusTxt;
        AppCompatButton acceptBtn, rejectBtn, trackBtn;
        CircleImageView userImg;
        LinearLayoutCompat buttonLinear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTxt = itemView.findViewById(R.id.orderTxt);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            addressTxt = itemView.findViewById(R.id.addressTxt);
            durationTxt = itemView.findViewById(R.id.durationTxt);
            orderStatusTxt = itemView.findViewById(R.id.orderStatusTxt);
            userImg = itemView.findViewById(R.id.userImg);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
            trackBtn = itemView.findViewById(R.id.trackBtn);
            buttonLinear = itemView.findViewById(R.id.buttonLinear);
        }
    }
}