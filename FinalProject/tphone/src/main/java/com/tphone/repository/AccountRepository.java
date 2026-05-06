package com.tphone.repository;

import com.tphone.entity.Account;
import com.tphone.enums.AccountProvider;
import com.tphone.enums.AccountStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    List<Account> findAllByStatus(AccountStatus status);

    List<Account> findAllByProvider(AccountProvider provider);

    Optional<Account> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<Account> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = {"roles"})
    List<Account> findAllByDeletedAtIsNull();

    @EntityGraph(attributePaths = {"roles"})
    @Query("""
            select distinct a
            from Account a
            left join fetch a.roles r
            where a.deletedAt is null
              and (
                    :keyword is null
                    or trim(:keyword) = ''
                    or lower(a.fullName) like lower(concat('%', :keyword, '%'))
                    or lower(a.email) like lower(concat('%', :keyword, '%'))
                    or lower(coalesce(a.phone, '')) like lower(concat('%', :keyword, '%'))
                  )
              and (
                    :status is null
                    or a.status = :status
                  )
            order by a.createdAt desc
            """)
    List<Account> searchForAdmin(
            @Param("keyword") String keyword,
            @Param("status") AccountStatus status
    );
}