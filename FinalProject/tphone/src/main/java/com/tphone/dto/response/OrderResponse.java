package com.tphone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderCode;
    private Long accountId;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private String shippingRecipientName;
    private String shippingRecipientPhone;
    private String shippingLine1;
    private String shippingLine2;
    private String shippingWard;
    private String shippingDistrict;
    private String shippingCity;
    private String shippingStateProvince;
    private String shippingPostalCode;
    private String shippingCountry;

    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;

    private BigDecimal subtotalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    private String note;
    private String internalNote;
    private LocalDateTime placedAt;

    private List<OrderItemResponse> items;
}