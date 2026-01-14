package com.smart.delivery;

import java.util.*;
import java.util.stream.IntStream;

public class RamRegistry implements DbContext {
    private final HashMap<UUID, Integer> accessTokens = new HashMap<>();
    private final HashMap<String, TokenPassPair> tokens = new HashMap<>();
    private record Restaurant(RestaurantInfo info, List<ProductInfo> products) {};
    private final List<AccountInfo> accounts = new ArrayList<>();
    private final List<Restaurant> restaurants = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    @Override
    public Optional<TokenPassPair> findPasswordTokenPairByUsername(String username) {
        return Optional.ofNullable(tokens.get(username));
    }

    @Override
    public void insertPasswordTokenPair(String username, TokenPassPair password_token_pair) {
        tokens.put(username, password_token_pair);
    }

    @Override
    public Optional<AccountInfo> findAccountInfoFromId(int account_id) {
        return account_id < 0 || account_id >= accounts.size() ? Optional.empty() : Optional.ofNullable(accounts.get(account_id));
    }

    @Override
    public int insertNewAccount(AccountInfo account_info) {
        int id = accounts.size();
        accounts.add(account_info);
        return id;
    }

    @Override
    public void insertNewAccessToken(UUID access_token, int id) {
        accessTokens.put(access_token, id);
    }

    @Override
    public int findAccountIdFromAccessToken(UUID access_token) {
        var out = accessTokens.get(access_token);
        return out == null ? -1 : out;
    }

    @Override
    public AccountInfo[] getAllAccounts() {
        return accounts.toArray(new AccountInfo[0]);
    }

    @Override
    public int insertNewRestaurant(RestaurantInfo info) {
        restaurants.add(new Restaurant(info, new ArrayList<>()));
        return restaurants.size()-1;
    }

    @Override
    public RestaurantInfo[] getAllRestaurants() {
        return restaurants.stream().map((rest) -> rest.info).toArray(RestaurantInfo[]::new);
    }

    @Override
    public void insertNewProductForRestaurant(int restaurant_id, ProductInfo product_info) {
        restaurants.get(restaurant_id).products.add(product_info);
    }

    @Override
    public Optional<ProductInfo[]> getAllProductsForRestaurant(int restaurant_id) {
        if(restaurant_id < 0 || restaurant_id >= restaurants.size()) return Optional.empty();
        return Optional.of(restaurants.get(restaurant_id).products.toArray(new ProductInfo[0]));
    }

    @Override
    public void insertNewOrder(int issued_by_whom, List<OrderItem> order) {
        orders.add(new Order(false, -1, issued_by_whom, order));
    }

    @Override
    public PendingOrder[] getPendingOrders() {
        return IntStream.range(0, orders.size())
                .filter((idx) -> !orders.get(idx).isCompleted())
                .mapToObj(idx -> new PendingOrder(idx, orders.get(idx).getOrderItems())).toArray(PendingOrder[]::new);
    }

    @Override
    public boolean acceptOrder(int accepted_by_whom, int id) {
        if(id >= orders.size()) return false;
        orders.get(id).setCompleted(true);
        orders.get(id).setCompletedByWhom(accepted_by_whom);
        return true;
    }
}
