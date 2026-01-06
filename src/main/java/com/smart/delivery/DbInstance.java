package com.smart.delivery;

import com.smart.delivery.utils.HashUtils;

import java.util.UUID;

public class DbInstance {
    private static final RamRegistry ram_registry = new RamRegistry();
    // helper function. Will be removed later once we have a persistent db
    private static int insert_account_from_parts(String username, String password, AccountType account_type) {
        byte[] pass_hash = HashUtils.sha256(password);
        UUID access_token = UUID.randomUUID();
        int account_id = ram_registry.insert_new_account(new AccountInfo(username, account_type));
        ram_registry.insert_new_access_token(access_token, account_id);
        ram_registry.insert_password_token_pair(username, new TokenPassPair(access_token, pass_hash));
        return account_id;
    }
    static {
        insert_account_from_parts("admin", "admin", AccountType.Admin);
        insert_account_from_parts("Ben", "eater", AccountType.Customer);
        insert_account_from_parts("Mike", "delivery", AccountType.DeliveryGuy);
        int john = insert_account_from_parts("John Dough", "1234", AccountType.Manager);
        int johns_pizzeria = ram_registry.insert_new_restaurant(new RestaurantInfo(john, "John's Pizza"));
        ProductInfo[] johns_pizzas = {
                new ProductInfo("Pepperoni Pizza", 10),
                new ProductInfo("Vegan Pizza", 16),
                new ProductInfo("Three Cheese Pizza", 19),
        };
        for (ProductInfo pizza : johns_pizzas) {
            ram_registry.insert_new_product_for_restaurant(johns_pizzeria, pizza);
        }
        int juans_taco_place = ram_registry.insert_new_restaurant(new RestaurantInfo(john, "Juan's Authentic Tacos"));
        ProductInfo[] juans_tacos = {
                new ProductInfo("Carne Asada Taco", 5),
                new ProductInfo("Chicken Taco", 4),
                new ProductInfo("Veggie Taco", 3),
                new ProductInfo("Fish Taco", 6),
                new ProductInfo("Shrimp Taco", 7),
                new ProductInfo("Carnitas Taco", 5),
                new ProductInfo("Chorizo Taco", 5),
                new ProductInfo("Barbacoa Taco", 6),
        };
        for (ProductInfo taco : juans_tacos) {
            ram_registry.insert_new_product_for_restaurant(juans_taco_place, taco);
        }
    };
    public static DbContext get_instance() {
        return ram_registry;
    }
}
