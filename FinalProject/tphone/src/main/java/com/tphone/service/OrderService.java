package com.tphone.service;

import com.tphone.dto.request.CreateOrderRequest;
import com.tphone.dto.request.UpdateOrderStatusRequest;
import com.tphone.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long accountId, CreateOrderRequest request);

    List<OrderResponse> getMyOrders(Long accountId);

    OrderResponse getMyOrderById(Long accountId, Long orderId);

    List<OrderResponse> getAllOrders(String keyword, String orderStatus, String paymentStatus);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
}