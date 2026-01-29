package com.smart.delivery.common.controller;

import com.smart.delivery.common.data.AccountInfo;
import com.smart.delivery.common.data.ProductInfo;
import com.smart.delivery.common.data.RestaurantInfo;
import com.smart.delivery.common.requests.AccessTokenRequest;
import com.smart.delivery.registry.DbContext;
import com.smart.delivery.utils.Auth;
import com.smart.delivery.utils.DbHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
public class CommonRequestController {
    @Autowired
    private DbContext dbContext;

    private Optional<AccountInfo> commonAuth(UUID accessToken) {
        return Auth.authByAccessToken(dbContext, accessToken);
    }

    record GetProductRequest(UUID accessToken, int restaurantId) {};
    @PostMapping(path = "get_products")
    public ResponseEntity<ProductInfo[]> getProducts(@RequestBody GetProductRequest request) {
        var user_opt = commonAuth(request.accessToken());
        if(user_opt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return dbContext.getAllProductsForRestaurant(request.restaurantId()).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping(path = "get_restaurants")
    public ResponseEntity<RestaurantInfo[]> getRestaurants(@RequestBody AccessTokenRequest request) {
        var user = commonAuth(request.getAccessToken());
        if(user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(dbContext.getAllRestaurants());
    }

    record LoginRequest(String username, byte[] password) {};
    record LoginResponse(UUID accessToken) {};
    @PostMapping(path = "login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return dbContext
                .findPasswordTokenPairByUsername(loginRequest.username())
                .filter(pair -> Arrays.equals(pair.getPass(), loginRequest.password()))
                .map(pair -> ResponseEntity.ok(new LoginResponse(pair.getToken())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping(path = "account_info")
    public ResponseEntity<AccountInfo> accountInfo(@RequestBody AccessTokenRequest accountInfoRequest) {
        return DbHelper
                .getAccountInfoFromAccessToken(dbContext, accountInfoRequest.getAccessToken())
                .map(account -> new ResponseEntity<>(account, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
