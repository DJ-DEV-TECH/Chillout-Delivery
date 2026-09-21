package com.app.chillout_delivery.model;

import java.util.List;

public class OrderPageResponse {
    private boolean status;
    private int code;
    private List<OrderResponse> data;
    private int totalPages;
    private int totalElements;
    private int pageNumber;
    private int pageData;
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

    public List<OrderResponse> getData() {
        return data;
    }

    public void setData(List<OrderResponse> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageData() {
        return pageData;
    }

    public void setPageData(int pageData) {
        this.pageData = pageData;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
