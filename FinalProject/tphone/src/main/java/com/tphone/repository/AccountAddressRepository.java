package com.tphone.repository;

import com.tphone.entity.AccountAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountAddressRepository extends JpaRepository<AccountAddress, Long> {

    List<AccountAddress> findAllByAccountIdAndDeletedAtIsNull(Long accountId);

    Optional<AccountAddress> findByIdAndAccountIdAndDeletedAtIsNull(Long id, Long accountId);

    Optional<AccountAddress> findByAccountIdAndIsDefaultShippingTrueAndDeletedAtIsNull(Long accountId);

    Optional<AccountAddress> findByAccountIdAndIsDefaultBillingTrueAndDeletedAtIsNull(Long accountId);
}