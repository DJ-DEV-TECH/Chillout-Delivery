package com.app.chillout_delivery.retrofit;

import android.util.Log;
import com.app.chillout_delivery.ChillOutApplication;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://64.227.190.95:8088/chillout-delivery/api/delivery-boy/";

    private static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(new AuthInterceptor(ChillOutApplication.getInstance()))
            .addInterceptor(provideHttpLoggingInterceptor())
            .build();

    private static Retrofit retrofit = null;

    public static Retrofit getLoginApiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }

    static HttpLoggingInterceptor provideHttpLoggingInterceptor(){
        return new HttpLoggingInterceptor(message ->
                Log.e("APILOGSTATUS===>", message)).setLevel(HttpLoggingInterceptor.Level.BODY);
    }
}