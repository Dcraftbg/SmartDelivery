package com.smart.delivery.common.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TokenPassPair {
    @Id
    private String username;
    private UUID token;
    private byte[] pass;
}
