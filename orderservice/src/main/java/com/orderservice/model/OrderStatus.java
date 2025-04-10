package com.orderservice.model;

import lombok.Builder;

@Builder
public class OrderStatus {

    public static final String PENDING = "pending";
    public static final String PROCESSING = "processing";
    public static final String SHIPPING = "shipping";
    public static final String DELIVERED = "delivered";
    public static final String CANCEL = "cancel";
}
