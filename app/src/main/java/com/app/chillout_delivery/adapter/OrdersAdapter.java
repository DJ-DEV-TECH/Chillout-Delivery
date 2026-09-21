package com.app.chillout_delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.model.OrderModel;
import com.app.chillout_delivery.model.OrderResponse;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    Context context;
    List<OrderResponse> list;

    public OrdersAdapter(Context context, List<OrderResponse> list) {
        this.context = context;
        this.list = list;
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

        Glide.with(holder.itemView.getContext())
                .load(model.getUserDetails().getImage())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.userImg);

        /*// Status Color
        switch (model.getOrderStatus()) {
            case "Pending":
                holder.txtStatus.setTextColor(0xFFFF9800);
                break;
            case "Completed":
                holder.txtStatus.setTextColor(0xFF4CAF50);
                break;
            case "Cancelled":
                holder.txtStatus.setTextColor(0xFFF44336);
                break;
        }*/

        // Assign Button
        holder.acceptBtn.setOnClickListener(v -> {
//            showAssignDialog(model.getOrderId());
            Toast.makeText(context, "Assign Delivery Boy: " + model.getOrderId(), Toast.LENGTH_SHORT).show();
        });

        // Track Button
        holder.rejectBtn.setOnClickListener(v -> {
            Toast.makeText(context, "Tracking Order: " + model.getOrderId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView orderTxt, userNameTxt, addressTxt, durationTxt;
        AppCompatButton acceptBtn, rejectBtn;
        CircleImageView userImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTxt = itemView.findViewById(R.id.orderTxt);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            addressTxt = itemView.findViewById(R.id.addressTxt);
            durationTxt = itemView.findViewById(R.id.durationTxt);
            userImg = itemView.findViewById(R.id.userImg);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
        }
    }
}