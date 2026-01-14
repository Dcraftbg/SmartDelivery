package com.smart.delivery.admin.requests;

import com.smart.delivery.AccessTokenRequest;
import com.smart.delivery.AccountInfo;
import com.smart.delivery.AccountType;
import com.smart.delivery.DbInstance;
import com.smart.delivery.utils.DbHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController()
@RequestMapping(path = "admin_rq")
public class AdminRequestController {
    // TODO: refactor this out
    private Optional<AccountInfo> adminAuth(UUID accessToken) {
        var user_opt = DbHelper.getAccountInfoFromAccessToken(DbInstance.get_instance(), accessToken);
        if(user_opt.isEmpty()) {
            System.err.println("Someone tried to login with a bogus token: " + accessToken);
            return user_opt;
        }
        var user = user_opt.get();
        if(user.getType() != AccountType.Admin) {
            System.err.println("Non admin " + accessToken + " tried to execute an admin request");
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @PostMapping(path = "get_users")
    public ResponseEntity<AccountInfo[]> getUsers(@RequestBody AccessTokenRequest request) {
        var user = adminAuth(request.getAccess_token());
        if(user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(DbInstance.get_instance().getAllAccounts());
    }
}
