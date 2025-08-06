package com.durianchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
@Getter
@Setter
@TableName("trader_agency")
public class TraderAgency implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String agencyId;

    private String ownerAddress;

    private String agencyName;

    private String exportLicenseCid;

    private LocalDateTime exportLicenseExpiry;

    private String txHash;

    private Boolean isApproved;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;


}
