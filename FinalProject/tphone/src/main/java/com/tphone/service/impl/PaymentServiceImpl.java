package com.tphone.service.impl;

import com.tphone.dto.request.CreatePaymentRequest;
import com.tphone.dto.request.UpdatePaymentStatusRequest;
import com.tphone.dto.response.PaymentResponse;
import com.tphone.entity.Order;
import com.tphone.entity.Payment;
import com.tphone.enums.PaymentProvider;
import com.tphone.enums.PaymentStatus;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.OrderRepository;
import com.tphone.repository.PaymentRepository;
import com.tphone.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponse createPayment(Long accountId, CreatePaymentRequest request) {
        if (request.getOrderId() == null) {
            throw new BadRequestException("Order id is required");
        }

        if (request.getProvider() == null || request.getProvider().isBlank()) {
            throw new BadRequestException("Payment provider is required");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getAccount().getId().equals(accountId)) {
            throw new BadRequestException("Order does not belong to current user");
        }

        PaymentProvider provider = parseProvider(request.getProvider());

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setTransactionCode(generateTransactionCode());
        payment.setProvider(provider);
        payment.setAmount(request.getAmount() != null ? request.getAmount() : order.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    @Override
    public List<PaymentResponse> getMyPayments(Long accountId) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getOrder() != null)
                .filter(payment -> payment.getOrder().getAccount() != null)
                .filter(payment -> payment.getOrder().getAccount().getId().equals(accountId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrder(Long accountId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getAccount().getId().equals(accountId)) {
            throw new BadRequestException("Order does not belong to current user");
        }

        return paymentRepository.findAllByOrderId(orderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long paymentId, UpdatePaymentStatusRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        PaymentStatus status = parseStatus(request.getStatus());

        payment.setStatus(status);
        payment.setProviderTransactionId(request.getProviderTransactionId());
        payment.setFailureReason(request.getFailureReason());
        payment.setRawResponse(request.getRawResponse());

        if (status == PaymentStatus.SUCCESS) {
            payment.setPaidAt(LocalDateTime.now());

            Order order = payment.getOrder();
            order.setPaymentStatus(com.tphone.enums.PaymentStatus.SUCCESS);
            orderRepository.save(order);
        }

        if (status == PaymentStatus.FAILED || status == PaymentStatus.CANCELLED) {
            Order order = payment.getOrder();
            order.setPaymentStatus(com.tphone.enums.PaymentStatus.FAILED);
            orderRepository.save(order);
        }

        return toResponse(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return toResponse(payment);
    }

    private PaymentProvider parseProvider(String value) {
        try {
            return PaymentProvider.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid payment provider");
        }
    }

    private PaymentStatus parseStatus(String value) {
        try {
            return PaymentStatus.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid payment status");
        }
    }

    private String generateTransactionCode() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder() != null ? payment.getOrder().getId() : null,
                payment.getOrder() != null ? payment.getOrder().getOrderCode() : null,
                payment.getTransactionCode(),
                payment.getProvider() != null ? payment.getProvider().name() : null,
                payment.getProviderTransactionId(),
                payment.getAmount(),
                payment.getStatus() != null ? payment.getStatus().name() : null,
                payment.getPaidAt(),
                payment.getFailureReason(),
                payment.getRawResponse(),
                payment.getCreatedAt()
        );
    }
}