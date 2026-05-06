package com.tphone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountAddressResponse {
    private Long id;
    private Long accountId;
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