package com.tphone.controller.client;

import com.tphone.dto.request.AddToCartRequest;
import com.tphone.dto.request.UpdateCartItemRequest;
import com.tphone.dto.response.ApiResponse;
import com.tphone.dto.response.CartResponse;
import com.tphone.security.model.CustomUserDetails;
import com.tphone.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getMyCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ApiResponse<>(true, "Get cart successfully", cartService.getMyCart(userDetails.getAccountId()));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request
    ) {
        return new ApiResponse<>(true, "Add to cart successfully",
                cartService.addToCart(userDetails.getAccountId(), request));
    }

    @PutMapping("/items/{cartItemId}")
    public ApiResponse<CartResponse> updateCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return new ApiResponse<>(true, "Update cart item successfully",
                cartService.updateCartItem(userDetails.getAccountId(), cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartResponse> removeCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId
    ) {
        return new ApiResponse<>(true, "Remove cart item successfully",
                cartService.removeCartItem(userDetails.getAccountId(), cartItemId));
    }

    @DeleteMapping("/clear")
    public ApiResponse<Object> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getAccountId());
        return new ApiResponse<>(true, "Cart cleared successfully", null);
    }
}