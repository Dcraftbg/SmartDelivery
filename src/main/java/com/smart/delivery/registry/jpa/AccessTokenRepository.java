package com.smart.delivery.registry.jpa;

import com.smart.delivery.common.data.TokenPassPair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<TokenPassPair, String> {

}
