package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrdersPaymentDTO implements Serializable {
    //订单号
    private Long orderId;

    //付款方式
    private Integer payMethod;

}
