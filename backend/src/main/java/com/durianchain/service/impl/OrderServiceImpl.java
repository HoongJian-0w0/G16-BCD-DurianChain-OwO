package com.durianchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.durianchain.common.result.ResultCode;
import com.durianchain.dto.OrderDTO;
import com.durianchain.entity.Batch;
import com.durianchain.exception.ServiceException;
import com.durianchain.mapper.BatchMapper;
import com.durianchain.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private BatchMapper batchMapper;

    @Override
    public boolean placeOrder(OrderDTO dto) {
        try {
            UpdateWrapper<Batch> wrapper = new UpdateWrapper<>();
            wrapper.eq("batch_id", dto.getBatchId());

            wrapper.set("status", "Ordered");
            wrapper.set("trader_agency_id", dto.getTraderAgencyId());
            wrapper.set("trader_address", dto.getTraderAddress());
            wrapper.set("delivery_destination", dto.getDeliveryDestination());
            wrapper.set("tx_hash", dto.getTxHash());

            int updated = batchMapper.update(null, wrapper);

            if (updated <= 0) {
                throw new ServiceException(ResultCode.BAD_REQUEST, "Batch not found or order update failed");
            }

            return true;

        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to update order: " + e.getMessage());
        }
    }

    @Override
    public boolean cancelOrder(OrderDTO dto) {
        try {
            UpdateWrapper<Batch> wrapper = new UpdateWrapper<>();
            wrapper.eq("batch_id", dto.getBatchId());

            wrapper.set("status", "Created");
            wrapper.set("trader_agency_id", null);
            wrapper.set("trader_address", null);
            wrapper.set("delivery_destination", null);

            wrapper.set("tx_hash", dto.getTxHash());

            int updated = batchMapper.update(null, wrapper);

            if (updated <= 0) {
                throw new ServiceException(ResultCode.BAD_REQUEST, "Batch not found or cancel failed");
            }

            return true;

        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to cancel order: " + e.getMessage());
        }
    }

}