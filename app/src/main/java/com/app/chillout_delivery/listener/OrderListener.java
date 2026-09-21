package com.app.chillout_delivery.listener;

import com.app.chillout_delivery.model.OrderResponse;

public interface OrderListener {
    void onOrderClick(OrderResponse orderModel);
}
