package com.smart.delivery.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Order {
    private boolean completed;
    private int completedByWhom;
    private final int issuedByWhom;
    private final List<OrderItem> orderItems;
}
