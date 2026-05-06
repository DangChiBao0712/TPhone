package com.tphone.controller.client;

import com.tphone.dto.request.AccountAddressRequest;
import com.tphone.dto.response.AccountAddressResponse;
import com.tphone.dto.response.MessageResponse;
import com.tphone.security.model.CustomUserDetails;
import com.tphone.service.AccountAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AccountAddressController {

    private final AccountAddressService accountAddressService;

    @GetMapping
    public List<AccountAddressResponse> getMyAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return accountAddressService.getMyAddresses(userDetails.getAccountId());
    }

    @GetMapping("/{addressId}")
    public AccountAddressResponse getMyAddressById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId
    ) {
        return accountAddressService.getMyAddressById(userDetails.getAccountId(), addressId);
    }

    @PostMapping
    public AccountAddressResponse createAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AccountAddressRequest request
    ) {
        return accountAddressService.createAddress(userDetails.getAccountId(), request);
    }

    @PutMapping("/{addressId}")
    public AccountAddressResponse updateAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId,
            @RequestBody AccountAddressRequest request
    ) {
        return accountAddressService.updateAddress(userDetails.getAccountId(), addressId, request);
    }

    @DeleteMapping("/{addressId}")
    public MessageResponse deleteAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId
    ) {
        accountAddressService.deleteAddress(userDetails.getAccountId(), addressId);
        return new MessageResponse("Address deleted successfully");
    }
}