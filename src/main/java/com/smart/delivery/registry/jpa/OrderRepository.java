package com.smart.delivery.registry.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<DbOrder,Integer> {
    List<DbOrder> findAllByCompleted(boolean completed);
}
