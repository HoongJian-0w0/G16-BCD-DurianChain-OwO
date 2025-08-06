package com.durianchain.service.impl;

import com.durianchain.service.IDGeneratorService;
import com.durianchain.common.util.IDGenerator;
import org.springframework.stereotype.Service;

@Service
public class IDGeneratorServiceImpl implements IDGeneratorService {

    @Override
    public String generateId(String type) {
        return switch (type.toLowerCase()) {
            case "farm" -> IDGenerator.generateFarmId();
            case "batch" -> IDGenerator.generateBatchId();
            case "durian" -> IDGenerator.generateDurianId();
            case "variety", "durian_variety" -> IDGenerator.generateVarietyId();
            case "agency", "trader_agency" -> IDGenerator.generateAgencyId();
            case "company", "logistics_company" -> IDGenerator.generateCompanyId();
            case "user" -> IDGenerator.generateUserId();
            case "tx", "wallet_tx" -> IDGenerator.generateTxId();
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

}