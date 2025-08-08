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
@TableName("wallet_tx")
public class WalletTx implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String walletAddress;

    private String role;

    private String action;

    private String referenceId;

    private String txHash;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
