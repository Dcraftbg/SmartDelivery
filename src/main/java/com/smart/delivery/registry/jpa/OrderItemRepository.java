package com.smart.delivery.registry.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<DbOrder.Item,Integer> {
    List<DbOrder.Item> findAllByOrderId(int id);
}
