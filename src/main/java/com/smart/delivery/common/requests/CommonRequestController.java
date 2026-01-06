package com.smart.delivery.common.requests;

import com.smart.delivery.*;
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
    private Optional<AccountInfo> common_auth(UUID access_token) {
        var user_opt = DbHelper.get_account_info_from_access_token(DbInstance.get_instance(), access_token);
        if(user_opt.isEmpty()) {
            System.err.println("Someone tried to login with a bogus token: " + access_token);
        }
        return user_opt;
    }
    @PostMapping(path = "get_products")
    public ResponseEntity<ProductInfo[]> get_products(@RequestBody GetProductRequest request) {
        var user_opt = common_auth(request.getAccess_token());
        if(user_opt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return DbInstance.get_instance().get_all_products_for_restaurant(request.getRestaurant_id()).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping(path = "get_restaurants")
    public ResponseEntity<RestaurantInfo[]> get_restaurants(@RequestBody AccessTokenRequest request) {
        var user = common_auth(request.getAccess_token());
        if(user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(DbInstance.get_instance().get_all_restaurants());
    }
    @PostMapping(path = "login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return DbInstance.get_instance()
                .find_password_token_pair_by_username(loginRequest.getUsername())
                .filter(pair -> Arrays.equals(pair.getPass(), loginRequest.getPassword()))
                .map(pair -> ResponseEntity.ok(new LoginResponse(pair.getToken().toString())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
    @PostMapping(path = "account_info")
    public ResponseEntity<AccountInfo> account_info(@RequestBody AccessTokenRequest accountInfoRequest) {
        return DbHelper
                .get_account_info_from_access_token(DbInstance.get_instance(), accountInfoRequest.getAccess_token())
                .map(account -> new ResponseEntity<>(account, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
