package com.durianchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.durianchain.common.result.ResultCode;
import com.durianchain.dto.ParcelDTO;
import com.durianchain.entity.Batch;
import com.durianchain.exception.ServiceException;
import com.durianchain.mapper.BatchMapper;
import com.durianchain.service.IParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParcelServiceImpl implements IParcelService {
    @Autowired
    private BatchMapper batchMapper;

    @Override
    public boolean collectParcel(ParcelDTO dto) {
        try {
            UpdateWrapper<Batch> wrapper = new UpdateWrapper<>();
            wrapper.eq("batch_id", dto.getBatchId());

            wrapper.set("status", "Collected");
            wrapper.set("logistics_company_id", dto.getLogisticsCompanyId());
            wrapper.set("logistics_address", dto.getLogisticsAddress());
            wrapper.set("tx_hash", dto.getTxHash());

            int updated = batchMapper.update(null, wrapper);

            if (updated <= 0) {
                throw new ServiceException(ResultCode.BAD_REQUEST, "Batch not found or collect update failed");
            }

            return true;

        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to collect parcel: " + e.getMessage());
        }
    }

    @Override
    public boolean deliverParcel(ParcelDTO dto) {
        try {
            UpdateWrapper<Batch> wrapper = new UpdateWrapper<>();
            wrapper.eq("batch_id", dto.getBatchId());

            wrapper.set("status", "Delivered");
            wrapper.set("tx_hash", dto.getTxHash());

            int updated = batchMapper.update(null, wrapper);

            if (updated <= 0) {
                throw new ServiceException(ResultCode.BAD_REQUEST, "Batch not found or delivery update failed");
            }

            return true;

        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to confirm delivery: " + e.getMessage());
        }
    }
}
