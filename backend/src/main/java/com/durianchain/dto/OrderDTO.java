package com.durianchain.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private String batchId;
    private String traderAgencyId;
    private String traderAddress;
    private String deliveryDestination;
    private String txHash;
}