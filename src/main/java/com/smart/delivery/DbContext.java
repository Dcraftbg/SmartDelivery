package com.smart.delivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DbContext {
    Optional<TokenPassPair> find_password_token_pair_by_username(String username);
    void insert_password_token_pair(String username, TokenPassPair password_token_pair);
    Optional<AccountInfo> find_account_info_from_id(int account_id);
    int insert_new_account(AccountInfo account_info);
    void insert_new_access_token(UUID access_token, int id);
    int find_account_id_from_access_token(UUID access_token);
    AccountInfo[] get_all_accounts();
    int insert_new_restaurant(RestaurantInfo info);
    RestaurantInfo[] get_all_restaurants();
    void insert_new_product_for_restaurant(int restaurant_id, ProductInfo product_info);
    Optional<ProductInfo[]> get_all_products_for_restaurant(int restaurant_id);
    void insert_new_order(int issued_by_whom, List<OrderItem> order);

    PendingOrder[] get_pending_orders();
    boolean accept_order(int accepted_by_whom, int id);
}
