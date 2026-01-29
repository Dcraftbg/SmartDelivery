package com.smart.delivery.registry.jpa;

import com.smart.delivery.common.data.ProductInfo;
import com.smart.delivery.common.data.RestaurantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<ProductInfo, Integer> {
    List<ProductInfo> findByRestaurantId(int restaurantId);
}
