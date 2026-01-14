package com.smart.delivery.delivery.requests;

import com.smart.delivery.*;
import com.smart.delivery.utils.DbHelper;
import lombok.Data;
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
    // TODO: refactor this out
    private Optional<AccountInfo> deliveryAuth(UUID access_token) {
        var user_opt = DbHelper.getAccountInfoFromAccessToken(DbInstance.get_instance(), access_token);
        if(user_opt.isEmpty()) {
            System.err.println("Someone tried to login with a bogus token: " + access_token);
            return user_opt;
        }
        var user = user_opt.get();
        if(user.getType() != AccountType.DeliveryGuy) {
            System.err.println("Non delivery " + access_token + " tried to issue a delivery request");
            return Optional.empty();
        }
        return Optional.of(user);
    }


    @PostMapping("pending_orders")
    public ResponseEntity<PendingOrder[]> pendingOrders(@RequestBody AccessTokenRequest request) {
        if(deliveryAuth(request.getAccessToken()).isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(DbInstance.get_instance().getPendingOrders());
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
        int author_id = DbInstance.get_instance().findAccountIdFromAccessToken(request.getAccessToken());
        List<Integer> successful = new ArrayList<>();
        for(int id : request.getOrders()) {
            if(DbInstance.get_instance().acceptOrder(author_id, id)) {
                successful.add(id);
            }
        }
        return ResponseEntity.ok(successful.toArray(new Integer[0]));
    }
}
