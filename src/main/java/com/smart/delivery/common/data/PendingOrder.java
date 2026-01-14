package com.smart.delivery.common.data;

import java.util.List;

public record PendingOrder(int id, List<OrderItem> orderItems) {
}
