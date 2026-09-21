package com.app.chillout_delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.model.OrderModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private List<OrderModel.Items> productList;
    private Context context;

    public OrderListAdapter(List<OrderModel.Items> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView itemImg;
        AppCompatTextView itemNameTxt;
        AppCompatTextView itemDescTxt;
        AppCompatTextView itemAmountTxt;
        AppCompatTextView itemQuatityTxt;

        public ViewHolder(View view) {
            super(view);
            itemImg = view.findViewById(R.id.itemImg);
            itemNameTxt = view.findViewById(R.id.itemNameTxt);
            itemDescTxt = view.findViewById(R.id.itemDescTxt);
            itemAmountTxt = view.findViewById(R.id.itemAmountTxt);
            itemQuatityTxt = view.findViewById(R.id.itemQuatityTxt);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderModel.Items product = productList.get(position);

        holder.itemNameTxt.setText(product.getItemName());
        holder.itemDescTxt.setText(product.getItemName());
        holder.itemAmountTxt.setText(""+product.getItemPrice());
        holder.itemQuatityTxt.setText("X "+product.getItemQty());

        Glide.with(holder.itemView.getContext())
                .load(product.getItemImage())
                .error(R.drawable.ic_home)
                .into(holder.itemImg);

        holder.itemView.setOnClickListener(v -> {
            // Handle item click here
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}