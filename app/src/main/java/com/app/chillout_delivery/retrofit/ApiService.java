package com.app.chillout_delivery.retrofit;

import com.app.chillout_delivery.model.DeliveryBoyStatusRequest;
import com.app.chillout_delivery.model.DeliveryDashboard;
import com.app.chillout_delivery.model.OrderPageResponse;
import com.app.chillout_delivery.model.UserModel;
import com.app.chillout_delivery.model.UserResponseModel;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth")
    Call<UserResponseModel> getLogin(@Body UserModel userModel);

    @POST("profile")
    Call<UserResponseModel> profile(@Body UserModel userModel);

    @PUT("status")
    Call<JsonElement> updateStatus(@Header("Authorization") String token,
                                   @Body DeliveryBoyStatusRequest deliveryBoyStatusRequest);

    @POST("orders/{orderId}/{status}")
    Call<JsonElement> updateOrderStatus(@Header("Authorization") String token,
                                   @Path("orderId") String orderId,
                                   @Path("status") String status);

    @POST("orders/getHistory")
    Call<OrderPageResponse> getOrders(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("dashboard")
    Call<DeliveryDashboard> getDashboard(
            @Header("Authorization") String token
    );

}
