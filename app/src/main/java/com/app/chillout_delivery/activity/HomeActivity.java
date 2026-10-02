package com.app.chillout_delivery.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
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
import com.app.chillout_delivery.fragment.ProfileFragment;
import com.app.chillout_delivery.fragment.WalletFragment;
import com.app.chillout_delivery.listener.OrderReceivedListener;
import com.app.chillout_delivery.model.DeliveryBoyStatusRequest;
import com.app.chillout_delivery.model.EventModel;
import com.app.chillout_delivery.model.OrderResponse;
import com.app.chillout_delivery.utils.EventManager;
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

        getOrderEvent();
//        socketManager = new SocketManager(this);
//        socketManager.connectSocket();
        binding.notifyImg.setOnClickListener(v -> {

        });

        if (status == 0 || status == 1) {
            binding.statusSwitch.setVisibility(VISIBLE);
            binding.deliveryLottie.setVisibility(GONE);
            binding.deliveryLottie.pauseAnimation();
        } else {
            binding.statusSwitch.setVisibility(GONE);
            binding.deliveryLottie.setVisibility(VISIBLE);
            binding.deliveryLottie.playAnimation();
        }

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
                    binding.statusSwitch.setChecked(status.getStatus() == 1);
                    EventManager.getInstance().sendEvent(
                            new EventModel("USER_STATUS", "", ""+status.getStatus()));
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

    private void getOrderEvent() {
        EventManager.getInstance().getEvents().observe(this, event -> {
            if (event == null) return;
            if (event.type.equals("USER_STATUS")) {
                String activeStatus = event.status;
                if (activeStatus.equals("0") || activeStatus.equals("1")) {
                    binding.statusSwitch.setVisibility(VISIBLE);
                    binding.statusSwitch.setChecked(activeStatus.equalsIgnoreCase("1"));
                    binding.deliveryLottie.setVisibility(GONE);
                    binding.deliveryLottie.pauseAnimation();
                } else {
                    binding.statusSwitch.setVisibility(GONE);
                    binding.deliveryLottie.setVisibility(VISIBLE);
                    binding.deliveryLottie.playAnimation();
                }
            }
        });
    }
}