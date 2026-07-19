package com.app.chillout_delivery.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.base.BaseActivity;
import com.app.chillout_delivery.databinding.ActivityHomeBinding;
import com.app.chillout_delivery.fragment.HistoryFragment;
import com.app.chillout_delivery.fragment.HomeFragment;
import com.app.chillout_delivery.fragment.PendingOrderFragment;
import com.app.chillout_delivery.fragment.ProfileFragment;
import com.app.chillout_delivery.fragment.WalletFragment;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.greyFinal));

        binding.notifyImg.setOnClickListener(v -> {
            Intent i = new Intent(this, OrderDetailsActivity.class);
            startActivity(i);
        });

        // Load HomeFragment by default
        loadFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_wallet) {
                selectedFragment = new WalletFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new PendingOrderFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });

        // Optional: set default selected item
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}