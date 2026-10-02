package com.app.chillout_delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.model.OrderModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    private List<OrderModel.Items> list;
    private Context context;

    public SummaryAdapter(List<OrderModel.Items> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView itemSummaryTxtCount, itemSummaryTxt, itemSummaryAmountTx;
        AppCompatImageView itemSummaryImg;

        public ViewHolder(View view) {
            super(view);
            itemSummaryTxtCount = view.findViewById(R.id.itemSummaryTxtCount);
            itemSummaryImg = view.findViewById(R.id.itemSummaryImg);
            itemSummaryTxt = view.findViewById(R.id.itemSummaryTxt);
            itemSummaryAmountTx = view.findViewById(R.id.itemSummaryAmountTx);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_summary_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel.Items item = list.get(position);

        holder.itemSummaryTxt.setText(item.getItemName());
        holder.itemSummaryAmountTx.setText("₹" + item.getItemPrice());
        holder.itemSummaryTxtCount.setText(item.getItemQty()+" x ");

        if (item.getItemImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getItemImage())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.itemSummaryImg);
        }

        holder.itemView.setOnClickListener(view -> {

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}