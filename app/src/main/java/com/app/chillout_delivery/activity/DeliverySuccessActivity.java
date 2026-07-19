package com.app.chillout_delivery.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.databinding.ActivityDeliverySuccessBinding;

public class DeliverySuccessActivity extends AppCompatActivity {

    ActivityDeliverySuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDeliverySuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        binding.goHomeBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finishAffinity();
        });
    }
}