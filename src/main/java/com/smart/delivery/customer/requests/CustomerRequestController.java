package com.smart.delivery.customer.requests;

import com.smart.delivery.AccountInfo;
import com.smart.delivery.AccountType;
import com.smart.delivery.DbInstance;
import com.smart.delivery.OrderItem;
import com.smart.delivery.utils.DbHelper;
import lombok.Data;
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
    // TODO: refactor this out
    private Optional<AccountInfo> customerAuth(UUID access_token) {
        var user_opt = DbHelper.getAccountInfoFromAccessToken(DbInstance.get_instance(), access_token);
        if(user_opt.isEmpty()) {
            System.err.println("Someone tried to login with a bogus token: " + access_token);
            return user_opt;
        }
        var user = user_opt.get();
        if(user.getType() != AccountType.Customer) {
            System.err.println("Non customer " + access_token + " tried to issue a customer request");
            return Optional.empty();
        }
        return Optional.of(user);
    }

    // TODO: think of a better way to do this
    public record ZeroResponse() {};
    @Data
    public static class PlaceOrderRequest {
        UUID access_token;
        List<OrderItem> order;
    }
    @PostMapping("place_order")
    public ResponseEntity<ZeroResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        var user = customerAuth(request.getAccess_token());
        if(user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // TODO: this is very wasteful, make a single request and return the id as well. Won't be a problem later with a Db but yeah
        int id = DbInstance.get_instance().findAccountIdFromAccessToken(request.getAccess_token());
        DbInstance.get_instance().insertNewOrder(id, request.order);
        return ResponseEntity.ok(new ZeroResponse());
    }
}
