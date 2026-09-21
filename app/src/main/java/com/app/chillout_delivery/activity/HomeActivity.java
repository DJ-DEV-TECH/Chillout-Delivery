package com.app.chillout_delivery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.app.chillout_delivery.listener.OrderReceivedListener;
import com.app.chillout_delivery.model.DeliveryBoyStatusRequest;
import com.app.chillout_delivery.model.UserResponseModel;
import com.app.chillout_delivery.retrofit.ApiClient;
import com.app.chillout_delivery.retrofit.ApiService;
import com.app.chillout_delivery.utils.PrefsHelper;
import com.app.chillout_delivery.utils.SocketManager;
import com.app.chillout_delivery.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements OrderReceivedListener {

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
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.grey10));

        socketManager = new SocketManager(this);
        socketManager.connectSocket();
        binding.notifyImg.setOnClickListener(v -> {
            Intent i = new Intent(this, OrderDetailsActivity.class);
            startActivity(i);
        });

        binding.statusSwitch.setChecked((status == 1));

        binding.statusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DeliveryBoyStatusRequest request = new DeliveryBoyStatusRequest();
            if (isChecked) {
                request.setStatus(1);
            } else {
                request.setStatus(0);
            }
            callStatusApi(request);
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
                selectedFragment = new HistoryFragment();
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

    @Override
    public void onOrderReceived() {
        playSound(this);
    }

    private void callStatusApi(DeliveryBoyStatusRequest status) {
        showLoading();
        Call<JsonElement> call = apiService.updateStatus(Utils.getAuthToken(authToken), status);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                closeLoading();
                if (response.isSuccessful()) {
                    prefsHelper.updateUserStatus(status.getStatus());
                    Intent intent = new Intent(STATUS_EVENT);
                    intent.setPackage(getPackageName());
                    intent.putExtra("status", status.getStatus());
                    sendBroadcast(intent);
                    Log.d("API", "Success: " + status);
                } else {
                    try {
                        String error = response.errorBody().string();
                        Log.e("API_ERROR", error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }
}