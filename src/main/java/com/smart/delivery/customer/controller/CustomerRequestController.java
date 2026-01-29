package com.smart.delivery.customer.controller;

import com.smart.delivery.common.data.AccountInfo;
import com.smart.delivery.common.data.AccountType;
import com.smart.delivery.common.data.OrderItem;
import com.smart.delivery.registry.DbContext;
import com.smart.delivery.utils.Auth;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "customer_rq")
public class CustomerRequestController {
    @Autowired
    private DbContext dbContext;

    private Optional<AccountInfo> customerAuth(UUID accessToken) {
        return Auth.authByAccountType(dbContext, accessToken, AccountType.Customer);
    }

    @Data
    public static class PlaceOrderRequest {
        UUID accessToken;
        List<OrderItem> order;
    }
    @PostMapping("place_order")
    public ResponseEntity<Void> placeOrder(@RequestBody PlaceOrderRequest request) {
        var user = customerAuth(request.getAccessToken());
        if(user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // TODO: this is very wasteful, make a single request and return the id as well. Won't be a problem later with a Db but yeah
        int id = dbContext.findAccountIdFromAccessToken(request.getAccessToken());
        dbContext.insertNewOrder(id, request.order);
        return ResponseEntity.ok().build();
    }
}
