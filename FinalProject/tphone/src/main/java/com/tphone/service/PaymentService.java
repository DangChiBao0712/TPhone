package com.tphone.service;

import com.tphone.dto.request.CreatePaymentRequest;
import com.tphone.dto.request.UpdatePaymentStatusRequest;
import com.tphone.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(Long accountId, CreatePaymentRequest request);
    List<PaymentResponse> getMyPayments(Long accountId);
    List<PaymentResponse> getPaymentsByOrder(Long accountId, Long orderId);
    PaymentResponse updatePaymentStatus(Long paymentId, UpdatePaymentStatusRequest request);

    List<PaymentResponse> getAllPayments();
    PaymentResponse getPaymentById(Long paymentId);
}