package com.app.chillout_delivery.model;

public class DeliveryDashboard {
    private Long totalOrders;
    private Long totalDelivered;
    private Long totalCancelled;
    private Long totalPending;

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalDelivered() {
        return totalDelivered;
    }

    public void setTotalDelivered(Long totalDelivered) {
        this.totalDelivered = totalDelivered;
    }

    public Long getTotalCancelled() {
        return totalCancelled;
    }

    public void setTotalCancelled(Long totalCancelled) {
        this.totalCancelled = totalCancelled;
    }

    public Long getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(Long totalPending) {
        this.totalPending = totalPending;
    }
}
