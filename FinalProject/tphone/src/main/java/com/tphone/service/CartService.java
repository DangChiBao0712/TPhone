package com.tphone.service;

import com.tphone.dto.request.AddToCartRequest;
import com.tphone.dto.request.UpdateCartItemRequest;
import com.tphone.dto.response.CartResponse;

public interface CartService {
    CartResponse getMyCart(Long accountId);
    CartResponse addToCart(Long accountId, AddToCartRequest request);
    CartResponse updateCartItem(Long accountId, Long cartItemId, UpdateCartItemRequest request);
    CartResponse removeCartItem(Long accountId, Long cartItemId);
    void clearCart(Long accountId);
}