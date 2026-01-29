package com.smart.delivery.registry.jpa;

import com.smart.delivery.common.data.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountInfo,Integer> {
}
