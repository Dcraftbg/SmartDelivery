package com.smart.delivery.common.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RestaurantInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private int managerWhoAddedEntry;
    private String name;
    public RestaurantInfo(int managerWhoAddedEntry, String name) {
        this.managerWhoAddedEntry = managerWhoAddedEntry;
        this.name = name;
    }
}
