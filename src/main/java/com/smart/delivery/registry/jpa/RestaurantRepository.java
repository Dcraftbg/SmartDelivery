package com.smart.delivery.registry.jpa;

import com.smart.delivery.common.data.RestaurantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantInfo, Integer> {
}
