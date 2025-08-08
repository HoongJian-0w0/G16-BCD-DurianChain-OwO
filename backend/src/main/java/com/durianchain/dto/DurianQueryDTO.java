package com.durianchain.dto;

import lombok.Data;

import java.util.List;

@Data
public class DurianQueryDTO {
    private String farmId;
    private String varietyId;
    private List<String> status;
    private String batchId;
}