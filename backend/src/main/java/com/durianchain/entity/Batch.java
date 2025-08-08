package com.durianchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Batch entity matching the DurianChain smart contract
 */
@Getter
@Setter
@TableName("batch")
public class Batch implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String batchId;

    private String foodName;

    private String farmId;

    private Integer quantity;

    private String farmLocation;

    private String batchImageCid;

    private String deliveryDestination;

    private String status;

    private String farmerAddress;

    private String traderAddress;

    private String traderAgencyId;

    private String logisticsCompanyId;

    private String logisticsAddress;

    private String txHash;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
