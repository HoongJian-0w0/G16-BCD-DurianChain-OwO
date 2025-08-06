package com.durianchain.common.util;

import java.util.UUID;

public class IDGenerator {

    public static String generate(String entity) {
        String prefix = switch (entity.toLowerCase()) {
            case "farm" -> "FARM";
            case "batch" -> "BATCH";
            case "variety", "durian_variety" -> "VARIETY";
            case "agency", "trader_agency" -> "AGENCY";
            case "company", "logistics_company" -> "COMPANY";
            case "durian" -> "DURIAN";
            case "user" -> "USER";
            case "wallet_tx", "tx" -> "TX";
            default -> "GEN"; // fallback prefix
        };

        return prefix + "-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12).toUpperCase();
    }

    public static String generateFarmId() { return generate("farm"); }
    public static String generateBatchId() { return generate("batch"); }
    public static String generateVarietyId() { return generate("variety"); }
    public static String generateAgencyId() { return generate("agency"); }
    public static String generateCompanyId() { return generate("company"); }
    public static String generateDurianId() { return generate("durian"); }
    public static String generateUserId() { return generate("user"); }
    public static String generateTxId() { return generate("tx"); }
}