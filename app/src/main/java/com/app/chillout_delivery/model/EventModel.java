package com.app.chillout_delivery.model;

public class EventModel {
    public String type; // NEW_ORDER / ORDER_UPDATE
    public String orderId;
    public String status;
    public String data; // optional JSON

    public EventModel() {
    }

    public EventModel(String type, String orderId, String status) {
        this.type = type;
        this.orderId = orderId;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
