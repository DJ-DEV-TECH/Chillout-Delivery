package com.app.chillout_delivery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.chillout_delivery.ChillOutApplication;
import com.app.chillout_delivery.R;
import com.app.chillout_delivery.base.BaseActivity;
import com.app.chillout_delivery.databinding.ActivityLoginBinding;
import com.app.chillout_delivery.model.UserModel;
import com.app.chillout_delivery.model.UserResponseModel;
import com.app.chillout_delivery.retrofit.ApiClient;
import com.app.chillout_delivery.retrofit.ApiService;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String otpValue;
    private boolean isOtpScreenVisible = false;
    private CountDownTimer countDownTimer;
    String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        binding.rippleSendOtpBtn.setOnClickListener(view -> {
            showLoading();
            countDown();
            isOtpScreenVisible = true;
            mobileNumber = binding.edMbleNum.getText().toString().trim();
            if (!mobileNumber.isEmpty()) {
                sendVerificationCode(mobileNumber);
            } else {
                Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            }
        });

        binding.otpView.setTextChangeListener((s, b) -> {
            otpValue = s;
        });

        binding.rippleVerifyOtpBtn.setOnClickListener(v -> {
            showLoading();
            verifyCode(otpValue);
        });

        binding.resendTxt.setOnClickListener(v -> {
            showLoading();
            countDown();
            mobileNumber = binding.edMbleNum.getText().toString().trim();
            if (!mobileNumber.isEmpty()) {
                sendVerificationCode(mobileNumber);
            } else {
                Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isOtpScreenVisible) {
                    isOtpScreenVisible = false;
                    switchView(binding.otpConstraint, binding.loginConstraint);
                } else {
                    finish();
                }
            }
        });

        binding.googleBtn.setOnClickListener(this);
        binding.fbBtn.setOnClickListener(this);
        binding.watsappBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.googleBtn || id == R.id.fbBtn || id == R.id.watsappBtn) {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLogin(UserModel userModel) {
        apiService.getLogin(userModel).enqueue(new retrofit2.Callback<UserResponseModel>() {
            @Override
            public void onResponse(retrofit2.Call<UserResponseModel> call,
                                   retrofit2.Response<UserResponseModel> response) {
                closeLoading();
                if (response.isSuccessful() && response.body() != null) {
                    UserResponseModel userResponseModel = response.body();
                    prefsHelper.saveUser(userResponseModel.getData());
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserResponseModel> call, Throwable t) {

            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks);
        // 🔥 Check condition
        if (resendToken != null) {
            builder.setForceResendingToken(resendToken);
        } else {
            Log.d("OTP", "First time OTP send");
        }
        PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // Auto verification or instant verification
                    System.out.println("Check_JK onVerificationCompleted smsCode: "+credential.getSmsCode());
                    signInWithCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    System.out.println("Check_JK onVerificationFailed : "+e.getMessage());
                    Toast.makeText(LoginActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String id,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(id, token);
                    closeLoading();
                    verificationId = id;
                    resendToken = token;
                    switchView(binding.loginConstraint, binding.otpConstraint);
                    binding.otpView.setFocusable(true);
                    Toast.makeText(LoginActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        UserModel userModel = new UserModel();
                        userModel.setMobile(mobileNumber);
                        userModel.setDevice_token(ChillOutApplication.FCM_TOKEN);
                        loadLogin(userModel);
                    } else {
                        Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void switchView(View hideView, View showView) {
        hideView.animate()
                .alpha(0f)
                .setDuration(250)
                .withEndAction(() -> {
                    hideView.setVisibility(View.GONE);

                    showView.setAlpha(0f);
                    showView.setVisibility(View.VISIBLE);

                    showView.animate()
                            .alpha(1f)
                            .setDuration(250)
                            .start();
                }).start();
    }

    public void countDown() {
        binding.resendTxt.setAlpha(0.5f);
        // 🛑 Stop previous timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                int remainingSeconds = seconds % 60;
                String time = String.format("%02d:%02d", minutes, remainingSeconds);
                binding.timerTxt.setText(time);
            }

            public void onFinish() {
                binding.resendTxt.setEnabled(true);
                binding.resendTxt.setAlpha(1f);
                binding.timerTxt.setText("00:00");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}