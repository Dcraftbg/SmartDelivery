package com.smart.delivery.common.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class GetProductRequest {
    private final UUID access_token;
    private final int restaurant_id;
}
