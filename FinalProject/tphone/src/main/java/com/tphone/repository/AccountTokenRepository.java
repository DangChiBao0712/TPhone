package com.tphone.repository;

import com.tphone.entity.AccountToken;
import com.tphone.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccountTokenRepository extends JpaRepository<AccountToken, Long> {

    Optional<AccountToken> findByToken(String token);

    List<AccountToken> findAllByAccountId(Long accountId);

    List<AccountToken> findAllByAccountIdAndTokenType(Long accountId, TokenType tokenType);

    Optional<AccountToken> findTopByAccountIdAndTokenTypeOrderByCreatedAtDesc(Long accountId, TokenType tokenType);

    List<AccountToken> findAllByTokenTypeAndExpiresAtBefore(TokenType tokenType, LocalDateTime time);

    List<AccountToken> findAllByUsedAtIsNullAndExpiresAtAfter(LocalDateTime time);
}