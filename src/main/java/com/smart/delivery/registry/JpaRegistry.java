package com.smart.delivery.registry;

import com.smart.delivery.common.data.*;
import com.smart.delivery.registry.jpa.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class JpaRegistry implements DbContext {
    private AccessTokenRepository accessTokenRepository;
    private AccountRepository accountRepository;
    private AccessTokenUserIdRepository accessTokenUserIdRepository;
    private RestaurantRepository restaurantRepository;
    private ProductsRepository productsRepository;
    private OrderItemRepository orderItemRepository;
    private OrderRepository orderRepository;

    @Override
    public Optional<TokenPassPair> findPasswordTokenPairByUsername(String username) {
        return accessTokenRepository.findById(username);
    }

    @Override
    public void insertPasswordTokenPair(TokenPassPair passwordTokenPair) {
        accessTokenRepository.save(passwordTokenPair);
    }

    @Override
    public Optional<AccountInfo> findAccountInfoFromId(int accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public int insertNewAccount(AccountInfo accountInfo) {
        return accountRepository.save(accountInfo).getId();
    }

    @Override
    public void insertNewAccessToken(UUID accessToken, int id) {
        accessTokenUserIdRepository.save(new AccessTokenIdPair(accessToken, id));
    }

    @Override
    public int findAccountIdFromAccessToken(UUID accessToken) {
        return accessTokenUserIdRepository.findById(accessToken).map(AccessTokenIdPair::getId).orElse(-1);
    }

    @Override
    public AccountInfo[] getAllAccounts() {
        // TODO: Okay definitely stupid to do a static array
        return accountRepository.findAll().toArray(new AccountInfo[0]);
    }

    @Override
    public int insertNewRestaurant(RestaurantInfo info) {
        return restaurantRepository.save(info).getId();
    }

    @Override
    public RestaurantInfo[] getAllRestaurants() {
        return restaurantRepository.findAll().toArray(new RestaurantInfo[0]);
    }

    @Override
    public void insertNewProductForRestaurant(int restaurantId, ProductInfo productInfo) {
        productInfo.setRestaurantId(restaurantId);
        productsRepository.save(productInfo);
    }

    @Override
    public Optional<ProductInfo[]> getAllProductsForRestaurant(int restaurantId) {
        return Optional.of(productsRepository.findByRestaurantId(restaurantId).toArray(new ProductInfo[0]));
    }

    @Override
    public void insertNewOrder(int issuedByWhom, List<OrderItem> order) {
        boolean completed = false;
        int completedByWhom = -1;
        var id = orderRepository.save(new DbOrder(completed, completedByWhom, issuedByWhom)).getOrderId();
        for (OrderItem item : order) {
            orderItemRepository.save(new DbOrder.Item(id, item.restaurantId(), item.productId(), item.count()));
        }
    }

    @Override
    public PendingOrder[] getPendingOrders() {
        var orders = orderRepository.findAllByCompleted(false);
        return orders.stream()
            .map(order -> new PendingOrder(order.getOrderId(),
                orderItemRepository.findAllByOrderId(order.getOrderId())
                    .stream()
                    .map(item -> new OrderItem(item.getRestaurantId(), item.getProductId(), item.getCount()))
                    .toList()
            )).toArray(PendingOrder[]::new);
    }

    @Override
    public boolean acceptOrder(int acceptedByWhom, int id) {
        var entryOpt = orderRepository.findById(id);
        if(!entryOpt.isPresent()) return false;
        var entry = entryOpt.get();
        entry.setCompleted(true);
        entry.setCompletedByWhom(acceptedByWhom);
        orderRepository.save(entry);
        return true;
    }
}
