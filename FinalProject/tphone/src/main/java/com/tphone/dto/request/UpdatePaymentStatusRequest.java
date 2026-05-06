package com.tphone.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePaymentStatusRequest {
    private String status;
    private String providerTransactionId;
    private String failureReason;
    private String rawResponse;
}