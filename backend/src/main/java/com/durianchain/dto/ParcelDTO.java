package com.durianchain.dto;

import lombok.Data;

@Data
public class ParcelDTO {

    private String batchId;
    private String logisticsCompanyId;
    private String logisticsAddress;
    private String txHash;

}