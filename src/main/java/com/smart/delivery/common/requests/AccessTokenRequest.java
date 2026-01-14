package com.smart.delivery.common.requests;

import lombok.Data;

import java.util.UUID;

// TODO: come up with a better name for this, it means a generic class that just has a UUID access_token
@Data
public class AccessTokenRequest {
    private final UUID accessToken;
}
