package com.smart.delivery.admin.controller;

import com.smart.delivery.common.requests.AccessTokenRequest;
import com.smart.delivery.common.data.AccountInfo;
import com.smart.delivery.common.data.AccountType;
import com.smart.delivery.registry.DbInstance;
import com.smart.delivery.utils.Auth;
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
    private Optional<AccountInfo> adminAuth(UUID accessToken) {
        return Auth.authByAccountType(accessToken, AccountType.Admin);
    }

    @PostMapping(path = "get_users")
    public ResponseEntity<AccountInfo[]> getUsers(@RequestBody AccessTokenRequest request) {
        var user = adminAuth(request.getAccessToken());
        if(user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(DbInstance.getInstance().getAllAccounts());
    }
}
