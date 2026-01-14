package com.smart.delivery.common.data;

import lombok.Data;

import java.util.UUID;

@Data
public class TokenPassPair {
    private final UUID token;
    private final byte[] pass;
}
