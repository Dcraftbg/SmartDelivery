package com.smart.delivery.config;

import com.smart.delivery.registry.DbContext;
import com.smart.delivery.registry.JpaRegistry;
import com.smart.delivery.registry.RamRegistry;
import com.smart.delivery.registry.jpa.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmartDeliveryConfig {
    @Bean
    @ConditionalOnProperty(name = "dbProvider", havingValue = "ram")
    public DbContext dbContextRam() {
        return new RamRegistry();
    }
    @Bean
    @ConditionalOnProperty(name = "dbProvider", havingValue = "jpa")
    public DbContext dbContextJpa(
            AccessTokenRepository accessTokenRepository,
            AccountRepository accountRepository,
            AccessTokenUserIdRepository accessTokenUserIdRepository,
            RestaurantRepository restaurantRepository,
            ProductsRepository productsRepository
    ) {
        return new JpaRegistry(accessTokenRepository, accountRepository, accessTokenUserIdRepository, restaurantRepository, productsRepository);
    }
}
