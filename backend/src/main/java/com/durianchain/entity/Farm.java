package com.durianchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
@Getter
@Setter
@TableName("farm")
public class Farm implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String farmId;

    private String ownerAddress;

    private String location;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String certificateCid;

    private LocalDateTime certificateExpiry;

    private String txHash;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;


}
