package com.app.chillout_delivery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.adapter.IntroViewPagerAdapter;
import com.app.chillout_delivery.databinding.ActivityOnboardingBinding;
import com.app.chillout_delivery.model.OnboardingItem;
import com.app.chillout_delivery.utils.PrefsHelper;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private IntroViewPagerAdapter introViewPagerAdapter;
    private List<OnboardingItem> mList = new ArrayList<>();
    private int position = 0;
    private Animation btnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        System.out.println("Check-JK SharedPref : "+restorePrefData());

        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(mainActivity);
            finish();
        }

        btnAnim = AnimationUtils.loadAnimation(this, R.anim.button_anim);

        mList.add(new OnboardingItem(R.drawable.food, "Fresh Food", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit"));
        mList.add(new OnboardingItem(R.drawable.food, "Fast Delivery", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit"));
        mList.add(new OnboardingItem(R.drawable.food, "Easy Payment", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit"));
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        binding.onboardingViewPager.setAdapter(introViewPagerAdapter);

        new TabLayoutMediator(binding.tabIndicator, binding.onboardingViewPager,
                (tab, position) -> {

                }).attach();

        binding.onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == mList.size() - 1) {
                    loadLastScreen();
                }
            }
        });

        binding.btnNext.setOnClickListener(view -> {
            position = binding.onboardingViewPager.getCurrentItem();
            if (position < mList.size()) {
                position++;
                binding.onboardingViewPager.setCurrentItem(position);
            }

            if (position == mList.size() - 1) {
                loadLastScreen();
            }
        });

        binding.btnSkip.setOnClickListener(view -> {
            binding.onboardingViewPager.setCurrentItem(mList.size());
        });

        binding.btnGetStarted.setOnClickListener(view -> {
            Intent mainActivity = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(mainActivity);
            savePrefsData();
            finish();
        });
    }

    private void loadLastScreen() {
        binding.btnNext.setVisibility(View.INVISIBLE);
        binding.btnGetStarted.setVisibility(View.VISIBLE);
        binding.tabIndicator.setVisibility(View.INVISIBLE);

        binding.btnGetStarted.setAnimation(btnAnim);
    }

    private boolean restorePrefData() {
        return  PrefsHelper.getBoolean("isIntroOpen");
    }

    private void savePrefsData() {
        PrefsHelper.saveBoolean("isIntroOpen", true);
    }
}