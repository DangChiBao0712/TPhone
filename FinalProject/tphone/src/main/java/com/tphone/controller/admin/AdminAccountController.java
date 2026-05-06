package com.tphone.controller.admin;

import com.tphone.dto.request.UpdateAccountStatusRequest;
import com.tphone.dto.response.AdminCustomerResponse;
import com.tphone.dto.response.ApiResponse;
import com.tphone.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping
    public ApiResponse<List<AdminCustomerResponse>> getCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return new ApiResponse<>(
                true,
                "Customer list",
                accountService.getAdminCustomers(keyword, status)
        );
    }

    @GetMapping("/{accountId}")
    public ApiResponse<AdminCustomerResponse> getCustomerById(@PathVariable Long accountId) {
        return new ApiResponse<>(
                true,
                "Customer detail",
                accountService.getAdminCustomerById(accountId)
        );
    }

    @PutMapping("/{accountId}/status")
    public ApiResponse<AdminCustomerResponse> updateStatus(
            @PathVariable Long accountId,
            @RequestBody UpdateAccountStatusRequest request
    ) {
        return new ApiResponse<>(
                true,
                "Update customer status successfully",
                accountService.updateAccountStatus(accountId, request)
        );
    }
}