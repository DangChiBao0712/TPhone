package com.tphone.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {
    private Long shippingAddressId;
    private Long billingAddressId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email is invalid")
    private String customerEmail;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    @NotBlank(message = "Shipping recipient name is required")
    private String shippingRecipientName;

    @NotBlank(message = "Shipping recipient phone is required")
    private String shippingRecipientPhone;

    @NotBlank(message = "Shipping line1 is required")
    private String shippingLine1;

    private String shippingLine2;
    private String shippingWard;
    private String shippingDistrict;

    @NotBlank(message = "Shipping city is required")
    private String shippingCity;

    private String shippingStateProvince;
    private String shippingPostalCode;
    private String shippingCountry;

    private String note;
    private String paymentMethod;
}