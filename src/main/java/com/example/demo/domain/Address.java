package com.example.demo.domain;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class Address {
    private Long memberId;
    private Long postcode;
    private String detailAddress;

    public Address(Long postcode, String detailAddress) {
        this.postcode = postcode;
        this.detailAddress = detailAddress;
    }

    public Address() {
    }
}
