package com.smart.delivery.utils;

import com.smart.delivery.common.data.AccountInfo;
import com.smart.delivery.common.data.AccountType;
import com.smart.delivery.registry.DbContext;

import java.util.Optional;
import java.util.UUID;

public class Auth {
    public static Optional<AccountInfo> authByAccessToken(DbContext context, UUID accessToken) {
        var userOpt = DbHelper.getAccountInfoFromAccessToken(context, accessToken);
        if(userOpt.isEmpty()) {
            System.err.println("Someone tried to login with a bogus token: " + accessToken);
        }
        return userOpt;
    }
    public static Optional<AccountInfo> authByAccountType(DbContext context, UUID accessToken, AccountType accountType) {
        var userOpt = authByAccessToken(context, accessToken);
        if(userOpt.isPresent()) {
            var user = userOpt.get();
            if (user.getType() != accountType) {
                System.err.println("Non " + accountType.toString() + " " + accessToken + " tried to issue a " + accountType.toString() + " request");
                return Optional.empty();
            }
        }
        return userOpt;
    }
}
