package com.tphone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String orderCode;
    private String transactionCode;
    private String provider;
    private String providerTransactionId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;
    private String failureReason;
    private String rawResponse;
    private LocalDateTime createdAt;
}