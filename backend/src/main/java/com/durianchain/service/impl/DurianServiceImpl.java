package com.durianchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.durianchain.entity.Durian;
import com.durianchain.mapper.DurianMapper;
import com.durianchain.service.IDurianService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  Service Implementation Class
 */
@Service
public class DurianServiceImpl extends ServiceImpl<DurianMapper, Durian> implements IDurianService {

    @Override
    public boolean releaseByBatchId(String batchId) {
        LambdaUpdateWrapper<Durian> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Durian::getBatchId, batchId)
                .eq(Durian::getStatus, "Assigned");

        Durian update = new Durian();
        update.setBatchId(null);
        update.setStatus("Pending");
        update.setOnChain(false);

        return this.update(update, wrapper);
    }

}
