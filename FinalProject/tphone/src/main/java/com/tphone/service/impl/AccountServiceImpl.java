package com.tphone.service.impl;

import com.tphone.dto.request.UpdateAccountStatusRequest;
import com.tphone.dto.request.UpdateProfileRequest;
import com.tphone.dto.response.AdminCustomerResponse;
import com.tphone.dto.response.CurrentUserResponse;
import com.tphone.entity.Account;
import com.tphone.enums.AccountStatus;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.AccountRepository;
import com.tphone.repository.OrderRepository;
import com.tphone.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;

    @Override
    public CurrentUserResponse getCurrentUser(Long accountId) {
        Account account = accountRepository.findByIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        return toCurrentUserResponse(account);
    }

    @Override
    @Transactional
    public CurrentUserResponse updateCurrentUser(Long accountId, UpdateProfileRequest request) {
        Account account = accountRepository.findByIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (request.getFullName() != null) {
            String fullName = request.getFullName().trim();
            if (fullName.isBlank()) {
                throw new BadRequestException("Full name must not be blank");
            }
            account.setFullName(fullName);
        }

        if (request.getPhone() != null) {
            String phone = normalizeNullable(request.getPhone());
            if (phone != null && accountRepository.existsByPhoneAndIdNot(phone, account.getId())) {
                throw new BadRequestException("Phone number already exists");
            }
            account.setPhone(phone);
        }

        if (request.getBirthDate() != null) {
            account.setBirthDate(request.getBirthDate());
        }

        if (request.getAvatarUrl() != null) {
            account.setAvatarUrl(normalizeNullable(request.getAvatarUrl()));
        }

        Account saved = accountRepository.save(account);
        return toCurrentUserResponse(saved);
    }

    @Override
    public List<AdminCustomerResponse> getAdminCustomers(String keyword, String status) {
        AccountStatus parsedStatus = parseStatusOrNull(status);

        return accountRepository.searchForAdmin(normalizeNullable(keyword), parsedStatus)
                .stream()
                .map(this::toAdminCustomerResponse)
                .toList();
    }

    @Override
    public AdminCustomerResponse getAdminCustomerById(Long accountId) {
        Account account = accountRepository.findByIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return toAdminCustomerResponse(account);
    }

    @Override
    @Transactional
    public AdminCustomerResponse updateAccountStatus(Long accountId, UpdateAccountStatusRequest request) {
        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            throw new BadRequestException("Status is required");
        }

        Account account = accountRepository.findByIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        account.setStatus(parseStatus(request.getStatus()));

        Account saved = accountRepository.save(account);
        return toAdminCustomerResponse(saved);
    }

    private CurrentUserResponse toCurrentUserResponse(Account account) {
        return new CurrentUserResponse(
                account.getId(),
                account.getEmail(),
                account.getFullName(),
                account.getPhone(),
                account.getBirthDate(),
                account.getAvatarUrl(),
                account.getStatus() != null ? account.getStatus().name() : null,
                account.getStatus() == AccountStatus.ACTIVE,
                account.getLastLoginAt(),
                account.getRoles().stream()
                        .map(role -> role.getCode())
                        .sorted()
                        .toList()
        );
    }

    private AdminCustomerResponse toAdminCustomerResponse(Account account) {
        Long totalOrders = orderRepository.countByAccountId(account.getId());
        BigDecimal totalSpent = orderRepository.sumSuccessfulTotalAmountByAccountId(account.getId());

        return new AdminCustomerResponse(
                account.getId(),
                account.getFullName(),
                account.getEmail(),
                account.getPhone(),
                account.getBirthDate(),
                account.getAvatarUrl(),
                account.getProvider() != null ? account.getProvider().name() : null,
                account.getStatus() != null ? account.getStatus().name() : null,
                account.getLastLoginAt(),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                account.getRoles().stream()
                        .map(role -> role.getCode())
                        .sorted()
                        .toList(),
                totalOrders != null ? totalOrders : 0L,
                totalSpent != null ? totalSpent : BigDecimal.ZERO
        );
    }

    private AccountStatus parseStatus(String value) {
        try {
            return AccountStatus.valueOf(value.trim().toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid account status");
        }
    }

    private AccountStatus parseStatusOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseStatus(value);
    }

    private String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}