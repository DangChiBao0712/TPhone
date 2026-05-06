package com.tphone.controller.admin;

import com.tphone.dto.request.UpdatePaymentStatusRequest;
import com.tphone.dto.response.PaymentResponse;
import com.tphone.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPaymentById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    @PutMapping("/{paymentId}/status")
    public PaymentResponse updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestBody UpdatePaymentStatusRequest request
    ) {
        return paymentService.updatePaymentStatus(paymentId, request);
    }
}