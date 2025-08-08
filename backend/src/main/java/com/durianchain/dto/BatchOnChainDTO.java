package com.durianchain.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchOnChainDTO {
    private String batchId;
    private String variety;
    private Integer quantity;
    private String farmId;
    private String batchImageCID;
    private List<String> durianIds;
    private List<String> imageHashes;
    private String farmerAddress;
    private String farmLocation;
    private String txHash;
}