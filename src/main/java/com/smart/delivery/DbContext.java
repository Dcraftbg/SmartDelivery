package com.smart.delivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DbContext {
    Optional<TokenPassPair> findPasswordTokenPairByUsername(String username);
    void insertPasswordTokenPair(String username, TokenPassPair password_token_pair);
    Optional<AccountInfo> findAccountInfoFromId(int account_id);
    int insertNewAccount(AccountInfo account_info);
    void insertNewAccessToken(UUID access_token, int id);
    int findAccountIdFromAccessToken(UUID access_token);
    AccountInfo[] getAllAccounts();
    int insertNewRestaurant(RestaurantInfo info);
    RestaurantInfo[] getAllRestaurants();
    void insertNewProductForRestaurant(int restaurant_id, ProductInfo product_info);
    Optional<ProductInfo[]> getAllProductsForRestaurant(int restaurant_id);
    void insertNewOrder(int issued_by_whom, List<OrderItem> order);

    PendingOrder[] getPendingOrders();
    boolean acceptOrder(int accepted_by_whom, int id);
}
