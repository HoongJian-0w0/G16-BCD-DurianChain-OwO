package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.dto.BatchOnChainDTO;
import com.durianchain.exception.ServiceException;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.durianchain.service.IBatchService;
import com.durianchain.entity.Batch;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController
@RequestMapping("/batch")
public class BatchController {

    @Resource
    private IBatchService batchService;

    @GetMapping
    public Result getAllBatch() {
        return Result.ok().data("list", batchService.list()).message("Fetched All Batch");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.ok().data("item", batchService.getById(id)).message("Fetched Batch by ID");
    }

    @PostMapping
    public Result save(@RequestBody Batch batch) {
        try {
                batchService.save(batch);
            return Result.ok().message("Batch Saved Successfully");
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to Save Batch");
        }
    }

    @PutMapping
    public Result update(@RequestBody Batch batch) {
        if (batchService.updateById(batch)) {
            return Result.ok().message("Batch Updated Successfully");
        }
        throw new ServiceException(ResultCode.NOT_FOUND, "Update Failed: Batch Not Found");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (batchService.removeById(id)) {
            return Result.ok().message("Batch Deleted Successfully");
        }
        return Result.error().message("Failed to Delete Batch");
    }

    @DeleteMapping("/del/batch/{ids}")
    public Result deleteBatch(@PathVariable List<Integer> ids) {
        if (batchService.removeByIds(ids)) {
            return Result.ok().message("Batchs Deleted Successfully");
        }
        return Result.error().message("Failed to Batch Delete Batchs");
    }

    @GetMapping("/page")
    public Result getPage(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(required = false) String batchId,
                          @RequestParam(required = false) String farmId,
                          @RequestParam(required = false) String foodName,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) List<String> statuses,
                          @RequestParam(required = false) String farmerAddress,
                          @RequestParam(required = false) String traderAddress,
                          @RequestParam(required = false) String logisticsAddress,
                          @RequestParam(required = false) String traderAgencyId,
                          @RequestParam(required = false) String logisticsCompanyId,
                          @RequestParam(required = false) Boolean onChain,
                          @RequestParam(required = false) Boolean traderNullOrMe,
                          @RequestParam(required = false) Boolean logisticsNullOrMe)
    {

        QueryWrapper<Batch> queryWrapper = new QueryWrapper<>();

        if (batchId != null && !batchId.isEmpty()) {
            queryWrapper.like("batch_id", batchId);
        }
        if (farmId != null && !farmId.isEmpty()) {
            queryWrapper.like("farm_id", farmId);
        }
        if (foodName != null && !foodName.isEmpty()) {
            queryWrapper.like("food_name", foodName);
        }

        if (statuses != null && !statuses.isEmpty()) {
            queryWrapper.in("status", statuses);
        } else if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        if (farmerAddress != null && !farmerAddress.isEmpty()) {
            queryWrapper.eq("farmer_address", farmerAddress);
        }

        if (Boolean.TRUE.equals(traderNullOrMe) && traderAddress != null && !traderAddress.isEmpty()) {
            queryWrapper.and(qw -> qw.isNull("trader_address").or().eq("trader_address", traderAddress));
        } else if (traderAddress != null && !traderAddress.isEmpty()) {
            queryWrapper.eq("trader_address", traderAddress);
        }

        if (Boolean.TRUE.equals(logisticsNullOrMe) && logisticsAddress != null && !logisticsAddress.isEmpty()) {
            queryWrapper.and(qw -> qw.isNull("logistics_address").or().eq("logistics_address", logisticsAddress));
        } else if (logisticsAddress != null && !logisticsAddress.isEmpty()) {
            queryWrapper.eq("logistics_address", logisticsAddress);
        }

        if (traderAgencyId != null && !traderAgencyId.isEmpty()) {
            queryWrapper.eq("trader_agency_id", traderAgencyId);
        }

        if (logisticsCompanyId != null && !logisticsCompanyId.isEmpty()) {
            queryWrapper.eq("logistics_company_id", logisticsCompanyId);
        }

        if (onChain != null) {
            queryWrapper.eq("on_chain", onChain);
        }

        queryWrapper.orderByDesc("id");

        Page<Batch> page = batchService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged Batch List with All Filters");
    }


    @PostMapping("/off-chain")
    public Result saveBatchOnChain(@RequestBody BatchOnChainDTO dto) {
        try {
            batchService.saveBatchFromChain(dto);
            return Result.ok().message("Batch saved and durians updated");
        } catch (Exception e) {
            return Result.error().message("Failed to save batch: " + e.getMessage());
        }
    }

    @GetMapping("/by-status")
    public Result getByStatus(@RequestParam(required = false) String status,
                              @RequestParam(required = false) List<String> statuses,
                              @RequestParam(required = false) String farmerAddress,
                              @RequestParam(required = false) String traderAddress,
                              @RequestParam(required = false) String logisticsAddress) {

        QueryWrapper<Batch> queryWrapper = new QueryWrapper<>();

        if (statuses != null && !statuses.isEmpty()) {
            queryWrapper.in("status", statuses);
        } else if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        if (farmerAddress != null && !farmerAddress.isEmpty()) {
            queryWrapper.eq("farmer_address", farmerAddress);
        }
        if (traderAddress != null && !traderAddress.isEmpty()) {
            queryWrapper.eq("trader_address", traderAddress);
        }
        if (logisticsAddress != null && !logisticsAddress.isEmpty()) {
            queryWrapper.eq("logistics_address", logisticsAddress);
        }

        queryWrapper.orderByDesc("id");

        List<Batch> list = batchService.list(queryWrapper);
        return Result.ok().data("list", list).message("Fetched Batches by Status");
    }

}
