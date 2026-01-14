package com.smart.delivery.common.requests;

import com.smart.delivery.*;
import com.smart.delivery.utils.DbHelper;
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
    private Optional<AccountInfo> commonAuth(UUID accessToken) {
        var user_opt = DbHelper.getAccountInfoFromAccessToken(DbInstance.get_instance(), accessToken);
        if(user_opt.isEmpty()) {
            System.err.println("Someone tried to login with a bogus token: " + accessToken);
        }
        return user_opt;
    }
    @PostMapping(path = "get_products")
    public ResponseEntity<ProductInfo[]> getProducts(@RequestBody GetProductRequest request) {
        var user_opt = commonAuth(request.getAccessToken());
        if(user_opt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return DbInstance.get_instance().getAllProductsForRestaurant(request.getRestaurantId()).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping(path = "get_restaurants")
    public ResponseEntity<RestaurantInfo[]> getRestaurants(@RequestBody AccessTokenRequest request) {
        var user = commonAuth(request.getAccessToken());
        if(user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(DbInstance.get_instance().getAllRestaurants());
    }
    @PostMapping(path = "login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return DbInstance.get_instance()
                .findPasswordTokenPairByUsername(loginRequest.getUsername())
                .filter(pair -> Arrays.equals(pair.getPass(), loginRequest.getPassword()))
                .map(pair -> ResponseEntity.ok(new LoginResponse(pair.getToken().toString())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
    @PostMapping(path = "account_info")
    public ResponseEntity<AccountInfo> accountInfo(@RequestBody AccessTokenRequest accountInfoRequest) {
        return DbHelper
                .getAccountInfoFromAccessToken(DbInstance.get_instance(), accountInfoRequest.getAccessToken())
                .map(account -> new ResponseEntity<>(account, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
