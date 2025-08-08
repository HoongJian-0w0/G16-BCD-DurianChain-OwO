package com.durianchain.service;

import com.durianchain.dto.BatchOnChainDTO;
import com.durianchain.entity.Batch;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *  Service Class
 */
public interface IBatchService extends IService<Batch> {

    boolean saveBatchFromChain(BatchOnChainDTO dto);
}
