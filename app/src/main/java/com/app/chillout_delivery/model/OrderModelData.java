package com.app.chillout_delivery.model;

public class OrderModelData {
    private boolean status;
    private int code;
    private OrderResponse data;
    private String msg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OrderResponse getData() {
        return data;
    }

    public void setData(OrderResponse data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
