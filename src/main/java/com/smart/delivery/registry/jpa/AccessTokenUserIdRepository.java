package com.smart.delivery.registry.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccessTokenUserIdRepository extends JpaRepository<AccessTokenIdPair, UUID> {

}
