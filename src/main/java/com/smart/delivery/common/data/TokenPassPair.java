package com.smart.delivery.common.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class TokenPassPair {
    @Id
    private String username;
    private final UUID token;
    private final byte[] pass;
}
