package com.smart.delivery.utils;

import com.smart.delivery.AccountInfo;
import com.smart.delivery.DbContext;

import java.util.Optional;
import java.util.UUID;

public class DbHelper {
    public static Optional<AccountInfo> get_account_info_from_access_token(DbContext context, UUID access_token) {
        int id = context.find_account_id_from_access_token(access_token);
        if(id < 0) {
            System.err.println("No account with id " + access_token + " found");
            return Optional.empty();
        }
        return context.find_account_info_from_id(id);
    }
}
