package com.smart.delivery.registry;

import com.smart.delivery.common.data.*;
import com.smart.delivery.registry.jpa.AccessTokenIdPair;
import com.smart.delivery.registry.jpa.AccessTokenRepository;
import com.smart.delivery.registry.jpa.AccessTokenUserIdRepository;
import com.smart.delivery.registry.jpa.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class JpaRegistry implements DbContext {
    private AccessTokenRepository accessTokenRepository;
    private AccountRepository accountRepository;
    private AccessTokenUserIdRepository accessTokenUserIdRepository;
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
        return 0;
    }

    @Override
    public RestaurantInfo[] getAllRestaurants() {
        return new RestaurantInfo[0];
    }

    @Override
    public void insertNewProductForRestaurant(int restaurantId, ProductInfo productInfo) {
    }

    @Override
    public Optional<ProductInfo[]> getAllProductsForRestaurant(int restaurantId) {
        return Optional.empty();
    }

    @Override
    public void insertNewOrder(int issuedByWhom, List<OrderItem> order) {
    }

    @Override
    public PendingOrder[] getPendingOrders() {
        return new PendingOrder[0];
    }

    @Override
    public boolean acceptOrder(int acceptedByWhom, int id) {
        return false;
    }
}
