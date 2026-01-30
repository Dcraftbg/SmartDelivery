package com.smart.delivery.registry.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class DbOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer orderId;
    private boolean completed;
    private int completedByWhom;
    private int issuedByWhom;

    public DbOrder(boolean completed, int completedByWhom, int issuedByWhom) {
        this.completed = completed;
        this.completedByWhom = completedByWhom;
        this.issuedByWhom = issuedByWhom;
    }

    @Data
    @Entity
    @NoArgsConstructor
    static public class Item {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private Integer itemId;
        private int orderId;
        private int restaurantId;
        private int productId;
        private int count;

        public Item(int orderId, int restaurantId, int productId, int count) {
            this.orderId = orderId;
            this.restaurantId = restaurantId;
            this.productId = productId;
            this.count = count;
        }
    };
}
