package com.durianchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.durianchain.common.result.ResultCode;
import com.durianchain.dto.BatchOnChainDTO;
import com.durianchain.entity.Batch;
import com.durianchain.entity.Durian;
import com.durianchain.exception.ServiceException;
import com.durianchain.mapper.BatchMapper;
import com.durianchain.mapper.DurianMapper;
import com.durianchain.service.IBatchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 *  Service Implementation Class
 */
@Service
public class BatchServiceImpl extends ServiceImpl<BatchMapper, Batch> implements IBatchService {

    @Autowired
    BatchMapper batchMapper;

    @Autowired
    DurianMapper durianMapper;

    @Override
    public boolean saveBatchFromChain(BatchOnChainDTO dto) {
        try {
            // 1. Check if batch already exists
            QueryWrapper<Batch> query = new QueryWrapper<>();
            query.eq("batch_id", dto.getBatchId());
            Batch existing = batchMapper.selectOne(query);

            Batch batch = new Batch();
            batch.setBatchId(dto.getBatchId());
            batch.setFoodName(dto.getVariety());
            batch.setFarmId(dto.getFarmId());
            batch.setQuantity(dto.getQuantity());
            batch.setBatchImageCid(dto.getBatchImageCID());
            batch.setFarmerAddress(dto.getFarmerAddress());
            batch.setFarmLocation(dto.getFarmLocation());
            batch.setTxHash(dto.getTxHash());
            batch.setStatus("Created");

            int affected;
            if (existing != null) {
                UpdateWrapper<Batch> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("batch_id", dto.getBatchId());
                affected = batchMapper.update(batch, updateWrapper);
            } else {
                affected = batchMapper.insert(batch);
            }

            if (affected <= 0) {
                return false;
            }

            UpdateWrapper<Durian> resetWrapper = new UpdateWrapper<>();
            resetWrapper.eq("batch_id", dto.getBatchId())
                    .set("batch_id", null)
                    .set("on_chain", false)
                    .set("status", "Pending");

            durianMapper.update(null, resetWrapper);

            for (int i = 0; i < dto.getDurianIds().size(); i++) {
                String durianId = dto.getDurianIds().get(i);
                String imageHash = dto.getImageHashes().get(i);

                UpdateWrapper<Durian> update = new UpdateWrapper<>();
                update.eq("durian_id", durianId)
                        .eq("image_hash", imageHash)
                        .set("batch_id", dto.getBatchId())
                        .set("on_chain", true)
                        .set("status", "Assigned");

                durianMapper.update(null, update);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Unexpected error during batch save");
        }
    }
}
