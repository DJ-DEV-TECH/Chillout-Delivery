package com.app.chillout_delivery.listener;

import com.app.chillout_delivery.model.OrderResponse;

public interface OrderStatusListener {
    void onOrderStatusUpdate(String type, OrderResponse orderResponse);
    void onOrderTrack(OrderResponse orderResponse);
}
