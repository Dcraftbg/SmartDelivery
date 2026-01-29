package com.smart.delivery.delivery.controller;

import com.smart.delivery.common.data.AccountInfo;
import com.smart.delivery.common.data.AccountType;
import com.smart.delivery.common.data.PendingOrder;
import com.smart.delivery.common.requests.AccessTokenRequest;
import com.smart.delivery.registry.DbContext;
import com.smart.delivery.utils.Auth;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "delivery_rq")
public class DeliveryRequestController {
    @Autowired
    private DbContext dbContext;
    private Optional<AccountInfo> deliveryAuth(UUID access_token) {
        return Auth.authByAccountType(dbContext, access_token, AccountType.DeliveryGuy);
    }


    @PostMapping("pending_orders")
    public ResponseEntity<PendingOrder[]> pendingOrders(@RequestBody AccessTokenRequest request) {
        if(deliveryAuth(request.getAccessToken()).isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(dbContext.getPendingOrders());
    }

    @Data
    public static class AcceptOrdersRequest {
        private final UUID accessToken;
        private final int[] orders;
    }
    @PostMapping("accept_orders")
    public ResponseEntity<Integer[]> acceptOrders(@RequestBody AcceptOrdersRequest request) {
        if(deliveryAuth(request.getAccessToken()).isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // TODO: stupid return id.
        int author_id = dbContext.findAccountIdFromAccessToken(request.getAccessToken());
        List<Integer> successful = new ArrayList<>();
        for(int id : request.getOrders()) {
            if(dbContext.acceptOrder(author_id, id)) {
                successful.add(id);
            }
        }
        return ResponseEntity.ok(successful.toArray(new Integer[0]));
    }
}
