package com.tphone.repository;

import com.tphone.entity.Account;
import com.tphone.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByAccount(Account account);

    List<RefreshToken> findAllByAccountId(Long accountId);

    List<RefreshToken> findAllByAccountIdAndRevokedAtIsNull(Long accountId);

    List<RefreshToken> findAllByExpiresAtBefore(LocalDateTime time);

    void deleteByAccountId(Long accountId);
}