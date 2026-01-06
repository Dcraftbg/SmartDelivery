package com.smart.delivery.delivery.requests;

import com.smart.delivery.*;
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
    private Optional<AccountInfo> delivery_auth(UUID access_token) {
        var user_opt = DbHelper.get_account_info_from_access_token(DbInstance.get_instance(), access_token);
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
    public ResponseEntity<PendingOrder[]> pending_orders(@RequestBody AccessTokenRequest request) {
        if(delivery_auth(request.getAccess_token()).isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(DbInstance.get_instance().get_pending_orders());
    }

    @Data
    public static class AcceptOrdersRequest {
        private final UUID access_token;
        private final int[] orders;
    }
    @PostMapping("accept_orders")
    public ResponseEntity<Integer[]> accept_orders(@RequestBody AcceptOrdersRequest request) {
        if(delivery_auth(request.getAccess_token()).isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // TODO: stupid return id.
        int author_id = DbInstance.get_instance().find_account_id_from_access_token(request.getAccess_token());
        List<Integer> successful = new ArrayList<>();
        for(int id : request.getOrders()) {
            if(DbInstance.get_instance().accept_order(author_id, id)) {
                successful.add(id);
            }
        }
        return ResponseEntity.ok(successful.toArray(new Integer[0]));
    }
}
