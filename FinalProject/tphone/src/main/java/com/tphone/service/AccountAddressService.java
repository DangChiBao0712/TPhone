package com.tphone.service;

import com.tphone.dto.request.AccountAddressRequest;
import com.tphone.dto.response.AccountAddressResponse;

import java.util.List;

public interface AccountAddressService {
    List<AccountAddressResponse> getMyAddresses(Long accountId);
    AccountAddressResponse getMyAddressById(Long accountId, Long addressId);
    AccountAddressResponse createAddress(Long accountId, AccountAddressRequest request);
    AccountAddressResponse updateAddress(Long accountId, Long addressId, AccountAddressRequest request);
    void deleteAddress(Long accountId, Long addressId);
}