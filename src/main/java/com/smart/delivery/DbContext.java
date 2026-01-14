package com.smart.delivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DbContext {
    Optional<TokenPassPair> findPasswordTokenPairByUsername(String username);
    void insertPasswordTokenPair(String username, TokenPassPair passwordTokenPair);
    Optional<AccountInfo> findAccountInfoFromId(int accountId);
    int insertNewAccount(AccountInfo accountInfo);
    void insertNewAccessToken(UUID accessToken, int id);
    int findAccountIdFromAccessToken(UUID accessToken);
    AccountInfo[] getAllAccounts();
    int insertNewRestaurant(RestaurantInfo info);
    RestaurantInfo[] getAllRestaurants();
    void insertNewProductForRestaurant(int restaurantId, ProductInfo product_info);
    Optional<ProductInfo[]> getAllProductsForRestaurant(int restaurantId);
    void insertNewOrder(int issuedByWhom, List<OrderItem> order);

    PendingOrder[] getPendingOrders();
    boolean acceptOrder(int acceptedByWhom, int id);
}
