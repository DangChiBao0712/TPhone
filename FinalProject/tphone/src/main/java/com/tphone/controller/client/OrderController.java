package com.tphone.controller.client;

import com.tphone.dto.request.CreateOrderRequest;
import com.tphone.dto.response.OrderResponse;
import com.tphone.security.model.CustomUserDetails;
import com.tphone.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.createOrder(userDetails.getAccountId(), request);
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return orderService.getMyOrders(userDetails.getAccountId());
    }

    @GetMapping("/{orderId}")
    public OrderResponse getMyOrderById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId
    ) {
        return orderService.getMyOrderById(userDetails.getAccountId(), orderId);
    }
}