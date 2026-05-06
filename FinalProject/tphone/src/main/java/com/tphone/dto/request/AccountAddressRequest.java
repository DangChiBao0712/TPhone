package com.tphone.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountAddressRequest {
    private String recipientName;
    private String recipientPhone;
    private String line1;
    private String line2;
    private String ward;
    private String district;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
    private Boolean isDefaultShipping;
    private Boolean isDefaultBilling;
}