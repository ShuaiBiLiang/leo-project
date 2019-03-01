package com.leo.model;

import java.io.Serializable;
import java.util.List;


public class GetOrdersVo implements Serializable {
   private List<OrderDetail> orderDetails;
   private String name;

    public GetOrdersVo(List<OrderDetail> orderDetails, String name) {
        this.orderDetails = orderDetails;
        this.name = name;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public GetOrdersVo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
