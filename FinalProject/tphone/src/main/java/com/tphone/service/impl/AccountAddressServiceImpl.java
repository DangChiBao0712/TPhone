package com.tphone.service.impl;

import com.tphone.dto.request.AccountAddressRequest;
import com.tphone.dto.response.AccountAddressResponse;
import com.tphone.entity.Account;
import com.tphone.entity.AccountAddress;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.AccountAddressRepository;
import com.tphone.repository.AccountRepository;
import com.tphone.service.AccountAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountAddressServiceImpl implements AccountAddressService {

    private final AccountAddressRepository accountAddressRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<AccountAddressResponse> getMyAddresses(Long accountId) {
        return accountAddressRepository.findAllByAccountIdAndDeletedAtIsNull(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AccountAddressResponse getMyAddressById(Long accountId, Long addressId) {
        AccountAddress address = accountAddressRepository
                .findByIdAndAccountIdAndDeletedAtIsNull(addressId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return toResponse(address);
    }

    @Override
    @Transactional
    public AccountAddressResponse createAddress(Long accountId, AccountAddressRequest request) {
        validateRequest(request);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (Boolean.TRUE.equals(request.getIsDefaultShipping())) {
            clearDefaultShipping(accountId);
        }

        if (Boolean.TRUE.equals(request.getIsDefaultBilling())) {
            clearDefaultBilling(accountId);
        }

        AccountAddress address = new AccountAddress();
        address.setAccount(account);
        mapRequest(address, request);

        return toResponse(accountAddressRepository.save(address));
    }

    @Override
    @Transactional
    public AccountAddressResponse updateAddress(Long accountId, Long addressId, AccountAddressRequest request) {
        validateRequest(request);

        AccountAddress address = accountAddressRepository
                .findByIdAndAccountIdAndDeletedAtIsNull(addressId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (Boolean.TRUE.equals(request.getIsDefaultShipping())) {
            clearDefaultShipping(accountId);
        }

        if (Boolean.TRUE.equals(request.getIsDefaultBilling())) {
            clearDefaultBilling(accountId);
        }

        mapRequest(address, request);

        return toResponse(accountAddressRepository.save(address));
    }

    @Override
    public void deleteAddress(Long accountId, Long addressId) {
        AccountAddress address = accountAddressRepository
                .findByIdAndAccountIdAndDeletedAtIsNull(addressId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        address.setDeletedAt(LocalDateTime.now());
        accountAddressRepository.save(address);
    }

    private void validateRequest(AccountAddressRequest request) {
        if (request.getRecipientName() == null || request.getRecipientName().isBlank()) {
            throw new BadRequestException("Recipient name is required");
        }
        if (request.getRecipientPhone() == null || request.getRecipientPhone().isBlank()) {
            throw new BadRequestException("Recipient phone is required");
        }
        if (request.getLine1() == null || request.getLine1().isBlank()) {
            throw new BadRequestException("Address line1 is required");
        }
        if (request.getCity() == null || request.getCity().isBlank()) {
            throw new BadRequestException("City is required");
        }
    }

    private void mapRequest(AccountAddress address, AccountAddressRequest request) {
        address.setRecipientName(request.getRecipientName());
        address.setRecipientPhone(request.getRecipientPhone());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        address.setStateProvince(request.getStateProvince());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(
                request.getCountry() != null && !request.getCountry().isBlank()
                        ? request.getCountry()
                        : "Vietnam"
        );
        address.setIsDefaultShipping(Boolean.TRUE.equals(request.getIsDefaultShipping()));
        address.setIsDefaultBilling(Boolean.TRUE.equals(request.getIsDefaultBilling()));
    }

    private void clearDefaultShipping(Long accountId) {
        accountAddressRepository.findByAccountIdAndIsDefaultShippingTrueAndDeletedAtIsNull(accountId)
                .ifPresent(address -> {
                    address.setIsDefaultShipping(false);
                    accountAddressRepository.save(address);
                });
    }

    private void clearDefaultBilling(Long accountId) {
        accountAddressRepository.findByAccountIdAndIsDefaultBillingTrueAndDeletedAtIsNull(accountId)
                .ifPresent(address -> {
                    address.setIsDefaultBilling(false);
                    accountAddressRepository.save(address);
                });
    }

    private AccountAddressResponse toResponse(AccountAddress address) {
        return new AccountAddressResponse(
                address.getId(),
                address.getAccount() != null ? address.getAccount().getId() : null,
                address.getRecipientName(),
                address.getRecipientPhone(),
                address.getLine1(),
                address.getLine2(),
                address.getWard(),
                address.getDistrict(),
                address.getCity(),
                address.getStateProvince(),
                address.getPostalCode(),
                address.getCountry(),
                address.getIsDefaultShipping(),
                address.getIsDefaultBilling()
        );
    }
}