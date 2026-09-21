package com.app.chillout_delivery.model;

import java.util.List;

public class OrderModel {
    public String id;
    public String orderId;
    public int shopId;
    public int categoryId;
    public int offerId;
    public String orderedDate;
    public String updatedDate;
    public String orderStatus;
    public String transactionStatus;
    public String paymentType;
    public double deliveryAmount;
    public double totalAmount;
    public String deliveryAddress;
    public String userName;
    public long userId;
    public List<Items> items;
    public String totalPages;
    public String totalElements;
    public String pageNumber;
    public String pageData;
    public String msg;

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getShopId() {
        return shopId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getOfferId() {
        return offerId;
    }

    public String getOrderedDate() {
        return orderedDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public double getDeliveryAmount() {
        return deliveryAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserId() {
        return userId;
    }

    public List<Items> getItems() {
        return items;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public String getTotalElements() {
        return totalElements;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public String getPageData() {
        return pageData;
    }

    public String getMsg() {
        return msg;
    }

    public class Items {
        public int itemId;
        public String itemName;
        public String itemImage;
        public int itemQty;
        public double itemPrice;
        public double itemOfferPrice;
        public double itemFinalPrice;

        public int getItemId() {
            return itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public String getItemImage() {
            return itemImage;
        }

        public int getItemQty() {
            return itemQty;
        }

        public double getItemPrice() {
            return itemPrice;
        }

        public double getItemOfferPrice() {
            return itemOfferPrice;
        }

        public double getItemFinalPrice() {
            return itemFinalPrice;
        }
    }
}
