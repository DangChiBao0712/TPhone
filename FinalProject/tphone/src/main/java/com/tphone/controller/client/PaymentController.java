package com.tphone.controller.client;

import com.tphone.dto.request.CreatePaymentRequest;
import com.tphone.dto.response.PaymentResponse;
import com.tphone.security.model.CustomUserDetails;
import com.tphone.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreatePaymentRequest request
    ) {
        return paymentService.createPayment(userDetails.getAccountId(), request);
    }

    @GetMapping
    public List<PaymentResponse> getMyPayments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return paymentService.getMyPayments(userDetails.getAccountId());
    }

    @GetMapping("/order/{orderId}")
    public List<PaymentResponse> getPaymentsByOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId
    ) {
        return paymentService.getPaymentsByOrder(userDetails.getAccountId(), orderId);
    }
}