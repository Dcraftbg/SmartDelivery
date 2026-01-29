package com.smart.delivery.config;

import com.smart.delivery.registry.DbContext;
import com.smart.delivery.registry.JpaRegistry;
import com.smart.delivery.registry.RamRegistry;
import com.smart.delivery.registry.jpa.AccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SmartDeliveryConfig {
    @Bean
    @ConditionalOnProperty(name = "dbProvider", havingValue = "ram")
    public DbContext dbContextRam() {
        return new RamRegistry();
    }
    @Bean
    @ConditionalOnProperty(name = "dbProvider", havingValue = "jpa")
    public DbContext dbContextJpa(AccessTokenRepository accessTokenRepository) {
        return new JpaRegistry(accessTokenRepository);
    }
}
