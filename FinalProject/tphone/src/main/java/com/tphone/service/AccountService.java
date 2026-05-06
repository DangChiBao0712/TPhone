package com.tphone.service;

import com.tphone.dto.request.UpdateAccountStatusRequest;
import com.tphone.dto.request.UpdateProfileRequest;
import com.tphone.dto.response.AdminCustomerResponse;
import com.tphone.dto.response.CurrentUserResponse;

import java.util.List;

public interface AccountService {

    CurrentUserResponse getCurrentUser(Long accountId);

    CurrentUserResponse updateCurrentUser(Long accountId, UpdateProfileRequest request);

    List<AdminCustomerResponse> getAdminCustomers(String keyword, String status);

    AdminCustomerResponse getAdminCustomerById(Long accountId);

    AdminCustomerResponse updateAccountStatus(Long accountId, UpdateAccountStatusRequest request);
}