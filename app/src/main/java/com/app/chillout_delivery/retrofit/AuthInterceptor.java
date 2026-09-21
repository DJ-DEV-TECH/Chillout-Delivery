package com.app.chillout_delivery.retrofit;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.app.chillout_delivery.activity.LoginActivity;
import com.app.chillout_delivery.utils.PrefsHelper;
import com.google.gson.Gson;

import org.json.JSONObject;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        if (responseBody == null) return response;
        String bodyString = responseBody.string();
        try {
            JSONObject json = new JSONObject(bodyString);
            System.out.println("Check_JK intercept json : "+json.toString());
            if (json.has("code") && (json.getInt("code") == 401 || json.getInt("code") == 403)) {
                PrefsHelper.clearAll(context);
                new Handler(Looper.getMainLooper()).post(() -> {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                });
            }
        } catch (Exception e) {
            // ignore parsing error
            System.out.println("Check_JK intercept Error : "+e.getMessage());
        }
        // ✅ IMPORTANT: recreate response body
        MediaType contentType = responseBody.contentType();

        return response.newBuilder()
                .body(ResponseBody.create(bodyString, contentType))
                .build();
//        return response;
    }
}
