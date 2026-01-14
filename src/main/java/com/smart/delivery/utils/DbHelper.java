package com.smart.delivery.utils;

import com.smart.delivery.AccountInfo;
import com.smart.delivery.DbContext;

import java.util.Optional;
import java.util.UUID;

public class DbHelper {
    public static Optional<AccountInfo> getAccountInfoFromAccessToken(DbContext context, UUID accessToken) {
        int id = context.findAccountIdFromAccessToken(accessToken);
        if(id < 0) {
            System.err.println("No account with id " + accessToken + " found");
            return Optional.empty();
        }
        return context.findAccountInfoFromId(id);
    }
}
