package com.smart.delivery;

import java.util.List;

public record PendingOrder(int id, List<OrderItem> order_items) {
}
