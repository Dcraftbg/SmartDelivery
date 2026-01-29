package com.smart.delivery.registry;

import com.smart.delivery.common.data.*;
import com.smart.delivery.utils.HashUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DbDataInit {
    @Autowired
    private DbContext dbContext;

    private int insertAccountFromParts(String username, String password, AccountType accountType) {
        byte[] pass_hash = HashUtils.sha256(password);
        UUID access_token = UUID.randomUUID();
        int account_id = dbContext.insertNewAccount(new AccountInfo(username, accountType));
        dbContext.insertNewAccessToken(access_token, account_id);
        dbContext.insertPasswordTokenPair(username, new TokenPassPair(access_token, pass_hash));
        return account_id;
    }
    @PostConstruct
    void init() {
        assert(dbContext != null);
        // helper function. Will be removed later once we have a persistent db
        insertAccountFromParts("admin", "admin", AccountType.Admin);
        insertAccountFromParts("Ben", "eater", AccountType.Customer);
        insertAccountFromParts("Mike", "delivery", AccountType.DeliveryGuy);
        int john = insertAccountFromParts("John Dough", "1234", AccountType.Manager);
        int johnsPizzeria = dbContext.insertNewRestaurant(new RestaurantInfo(john, "John's Pizza"));
        ProductInfo[] johns_pizzas = {
                new ProductInfo("Pepperoni Pizza", 10),
                new ProductInfo("Vegan Pizza", 16),
                new ProductInfo("Three Cheese Pizza", 19),
        };
        for (ProductInfo pizza : johns_pizzas) {
            dbContext.insertNewProductForRestaurant(johnsPizzeria, pizza);
        }
        int juansTacoPlace = dbContext.insertNewRestaurant(new RestaurantInfo(john, "Juan's Authentic Tacos"));
        ProductInfo[] juansTacos = {
                new ProductInfo("Carne Asada Taco", 5),
                new ProductInfo("Chicken Taco", 4),
                new ProductInfo("Veggie Taco", 3),
                new ProductInfo("Fish Taco", 6),
                new ProductInfo("Shrimp Taco", 7),
                new ProductInfo("Carnitas Taco", 5),
                new ProductInfo("Chorizo Taco", 5),
                new ProductInfo("Barbacoa Taco", 6),
        };
        for (ProductInfo taco : juansTacos) {
            dbContext.insertNewProductForRestaurant(juansTacoPlace, taco);
        }
    }
}
