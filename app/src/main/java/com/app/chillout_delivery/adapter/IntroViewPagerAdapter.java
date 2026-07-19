package com.app.chillout_delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.chillout_delivery.databinding.LayoutScreenBinding;
import com.app.chillout_delivery.model.OnboardingItem;

import java.util.List;

public class IntroViewPagerAdapter extends RecyclerView.Adapter<IntroViewPagerAdapter.OnboardingViewHolder> {

    private Context context;
    private List<OnboardingItem> items;

    public IntroViewPagerAdapter(Context context, List<OnboardingItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LayoutScreenBinding binding = LayoutScreenBinding.inflate(inflater, parent, false);
        return new OnboardingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnboardingItem item = items.get(position);
        holder.binding.introTitle.setText(item.getTitle());
        holder.binding.introDescription.setText(item.getDescription());
        holder.binding.introImg.setImageResource(item.getImageRes());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        LayoutScreenBinding binding;

        public OnboardingViewHolder(@NonNull LayoutScreenBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}