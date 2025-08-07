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

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;


}
