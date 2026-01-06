package com.smart.delivery.common.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private byte[] password;
}
