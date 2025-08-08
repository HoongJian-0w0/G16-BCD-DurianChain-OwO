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
@TableName("durian")
public class Durian implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String durianId;

    private String imageUrl;

    private String imageHash;

    private String farmId;

    private String batchId;

    private String varietyId;

    private String walletAddress;

    private Integer scanCount;

    private Boolean onChain;

    private String status;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
