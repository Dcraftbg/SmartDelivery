package com.smart.delivery.registry;

import com.smart.delivery.common.data.*;
import com.smart.delivery.utils.HashUtils;

import java.util.UUID;

public class DbInstance {
    private static final RamRegistry ramRegistry = new RamRegistry();
    // helper function. Will be removed later once we have a persistent db
    private static int insertAccountFromParts(String username, String password, AccountType accountType) {
        byte[] pass_hash = HashUtils.sha256(password);
        UUID access_token = UUID.randomUUID();
        int account_id = ramRegistry.insertNewAccount(new AccountInfo(username, accountType));
        ramRegistry.insertNewAccessToken(access_token, account_id);
        ramRegistry.insertPasswordTokenPair(username, new TokenPassPair(access_token, pass_hash));
        return account_id;
    }
    static {
        insertAccountFromParts("admin", "admin", AccountType.Admin);
        insertAccountFromParts("Ben", "eater", AccountType.Customer);
        insertAccountFromParts("Mike", "delivery", AccountType.DeliveryGuy);
        int john = insertAccountFromParts("John Dough", "1234", AccountType.Manager);
        int johnsPizzeria = ramRegistry.insertNewRestaurant(new RestaurantInfo(john, "John's Pizza"));
        ProductInfo[] johns_pizzas = {
                new ProductInfo("Pepperoni Pizza", 10),
                new ProductInfo("Vegan Pizza", 16),
                new ProductInfo("Three Cheese Pizza", 19),
        };
        for (ProductInfo pizza : johns_pizzas) {
            ramRegistry.insertNewProductForRestaurant(johnsPizzeria, pizza);
        }
        int juansTacoPlace = ramRegistry.insertNewRestaurant(new RestaurantInfo(john, "Juan's Authentic Tacos"));
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
            ramRegistry.insertNewProductForRestaurant(juansTacoPlace, taco);
        }
    };
    public static DbContext getInstance() {
        return ramRegistry;
    }
}
