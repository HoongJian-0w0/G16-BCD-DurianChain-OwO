package com.durianchain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
@Getter
@Setter
@TableName("logistics_company")
public class LogisticsCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String companyId;

    private String ownerAddress;

    private String companyName;

    private String txHash;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
