package com.smart.delivery.registry;

import com.smart.delivery.common.data.*;

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
    public void insertPasswordTokenPair(TokenPassPair passwordTokenPair) {
        tokens.put(passwordTokenPair.getUsername(), passwordTokenPair);
    }

    @Override
    public Optional<AccountInfo> findAccountInfoFromId(int accountId) {
        return accountId < 0 || accountId >= accounts.size() ? Optional.empty() : Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public int insertNewAccount(AccountInfo accountInfo) {
        int id = accounts.size();
        System.out.println("insertNewAccount: " + id);
        accountInfo.setId(id);
        accounts.add(accountInfo);
        return id;
    }

    @Override
    public void insertNewAccessToken(UUID accessToken, int id) {
        accessTokens.put(accessToken, id);
    }

    @Override
    public int findAccountIdFromAccessToken(UUID accessToken) {
        var out = accessTokens.get(accessToken);
        return out == null ? -1 : out;
    }

    @Override
    public AccountInfo[] getAllAccounts() {
        return accounts.toArray(new AccountInfo[0]);
    }

    @Override
    public int insertNewRestaurant(RestaurantInfo info) {
        info.setId(restaurants.size());
        restaurants.add(new Restaurant(info, new ArrayList<>()));
        return info.getId();
    }

    @Override
    public RestaurantInfo[] getAllRestaurants() {
        return restaurants.stream().map((rest) -> rest.info).toArray(RestaurantInfo[]::new);
    }

    @Override
    public void insertNewProductForRestaurant(int restaurantId, ProductInfo productInfo) {
        productInfo.setRestaurantId(restaurantId);
        productInfo.setId(restaurants.get(restaurantId).products.size());
        restaurants.get(restaurantId).products.add(productInfo);
    }

    @Override
    public Optional<ProductInfo[]> getAllProductsForRestaurant(int restaurantId) {
        if(restaurantId < 0 || restaurantId >= restaurants.size()) return Optional.empty();
        return Optional.of(restaurants.get(restaurantId).products.toArray(new ProductInfo[0]));
    }

    @Override
    public void insertNewOrder(int issuedByWhom, List<OrderItem> order) {
        orders.add(new Order(false, -1, issuedByWhom, order));
    }

    @Override
    public PendingOrder[] getPendingOrders() {
        return IntStream.range(0, orders.size())
                .filter((idx) -> !orders.get(idx).isCompleted())
                .mapToObj(idx -> new PendingOrder(idx, orders.get(idx).getOrderItems())).toArray(PendingOrder[]::new);
    }

    @Override
    public boolean acceptOrder(int acceptedByWhom, int id) {
        if(id >= orders.size()) return false;
        orders.get(id).setCompleted(true);
        orders.get(id).setCompletedByWhom(acceptedByWhom);
        return true;
    }
}
