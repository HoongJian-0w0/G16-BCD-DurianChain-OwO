package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.dto.DurianHashDTO;
import com.durianchain.dto.DurianIdListDTO;
import com.durianchain.dto.DurianQueryDTO;
import com.durianchain.exception.ServiceException;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.durianchain.service.IDurianService;
import com.durianchain.entity.Durian;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController
@RequestMapping("/durian")
public class DurianController {

    @Resource
    private IDurianService durianService;

    @GetMapping
    public Result getAllDurian() {
        return Result.ok().data("list", durianService.list()).message("Fetched All Durian");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.ok().data("item", durianService.getById(id)).message("Fetched Durian by ID");
    }

    @PostMapping
    public Result save(@RequestBody Durian durian) {
        try {
                durianService.save(durian);
            return Result.ok().message("Durian Saved Successfully");
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to Save Durian");
        }
    }

    @PutMapping
    public Result update(@RequestBody Durian durian) {
        if (durianService.updateById(durian)) {
            return Result.ok().message("Durian Updated Successfully");
        }
        throw new ServiceException(ResultCode.NOT_FOUND, "Update Failed: Durian Not Found");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (durianService.removeById(id)) {
            return Result.ok().message("Durian Deleted Successfully");
        }
        return Result.error().message("Failed to Delete Durian");
    }

    @DeleteMapping("/del/batch/{ids}")
    public Result deleteBatch(@PathVariable List<Integer> ids) {
        if (durianService.removeByIds(ids)) {
            return Result.ok().message("Durians Deleted Successfully");
        }
        return Result.error().message("Failed to Batch Delete Durians");
    }

    @GetMapping("/page")
    public Result getPage(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(required = false) String durianId,
                          @RequestParam(required = false) String farmId,
                          @RequestParam(required = false) String batchId,
                          @RequestParam(required = false) String varietyId,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Boolean onChain,
                          @RequestParam(required = false) String walletAddress) {

        QueryWrapper<Durian> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(durianId)) {
            queryWrapper.like("durian_id", durianId);
        }
        if (StringUtils.hasText(farmId)) {
            queryWrapper.eq("farm_id", farmId);
        }
        if (StringUtils.hasText(batchId)) {
            queryWrapper.eq("batch_id", batchId);
        }
        if (StringUtils.hasText(varietyId)) {
            queryWrapper.eq("variety_id", varietyId);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        if (onChain != null) {
            queryWrapper.eq("on_chain", onChain);
        }
        if (StringUtils.hasText(walletAddress)) {
            queryWrapper.eq("wallet_address", walletAddress);
        }

        queryWrapper.orderByDesc("id");

        Page<Durian> page = durianService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged Durian List with Filters");
    }

    @GetMapping("/by-farm-and-variety")
    public Result getByFarmAndVariety(@ModelAttribute DurianQueryDTO dto) {
        QueryWrapper<Durian> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("farm_id", dto.getFarmId());
        queryWrapper.eq("variety_id", dto.getVarietyId());

        boolean hasBatchId = dto.getBatchId() != null && !dto.getBatchId().isEmpty();

        if (hasBatchId) {
            // Custom logic: (status = 'Pending') OR (status = 'Assigned' AND batch_id = {batchId})
            queryWrapper.and(wrapper ->
                    wrapper.eq("status", "Pending")
                            .or(w -> w.eq("status", "Assigned").eq("batch_id", dto.getBatchId()))
            );
        } else if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            // Normal case: just filter by status list
            queryWrapper.in("status", dto.getStatus());
        }

        queryWrapper.orderByDesc("id");

        List<Durian> durians = durianService.list(queryWrapper);

        return Result.ok()
                .data("durians", durians)
                .message("Filtered durians by farm, variety"
                        + (hasBatchId ? " + (Pending OR Assigned in batchId)"
                        : (dto.getStatus() != null && !dto.getStatus().isEmpty() ? ", status" : ""))
                );
    }

    @PostMapping("/hash-list")
    public Result getHashesByIds(@RequestBody DurianIdListDTO request) {
        List<String> durianIds = request.getDurianIds();

        if (durianIds == null || durianIds.isEmpty()) {
            return Result.error().message("No durian IDs provided");
        }

        LambdaQueryWrapper<Durian> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Durian::getDurianId, durianIds);

        List<Durian> durians = durianService.list(wrapper);

        if (durians == null || durians.isEmpty()) {
            return Result.error().message("No durians found for provided IDs");
        }

        List<DurianHashDTO> hashes = durians.stream()
                .map(d -> new DurianHashDTO(d.getDurianId(), d.getImageHash()))
                .collect(Collectors.toList());

        return Result.ok().data("hashes", hashes).message("Fetched durian ID + hash list");
    }

    @GetMapping("/by-batch/{batchId}")
    public Result getDuriansByBatchId(@PathVariable String batchId) {
        if (!StringUtils.hasText(batchId)) {
            return Result.error().message("Batch ID must be provided");
        }

        LambdaQueryWrapper<Durian> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Durian::getBatchId, batchId)
                .select(Durian::getDurianId, Durian::getImageUrl, Durian::getImageHash)
                .orderByDesc(Durian::getId);

        List<Durian> durians = durianService.list(queryWrapper);

        return Result.ok()
                .data("durians", durians)
                .message("Fetched durians for batch: " + batchId);
    }

    @PutMapping("/release/{batchId}")
    public Result releaseDuriansFromBatch(@PathVariable String batchId) {
        if (!StringUtils.hasText(batchId)) {
            return Result.error().message("Batch ID is required");
        }

        boolean success = durianService.releaseByBatchId(batchId);
        if (success) {
            return Result.ok().message("Durians released from batch: " + batchId);
        } else {
            return Result.error().message("No durians were updated or batch not found");
        }
    }

    @GetMapping("/scan/{durianId}")
    public Result scan(@PathVariable String durianId) {
        if (!StringUtils.hasText(durianId)) {
            return Result.error().message("Durian ID is required");
        }

        Durian updatedDurian = durianService.incrementScanCountAndReturn(durianId);

        if (updatedDurian != null) {
            return Result.ok()
                    .data("durian", updatedDurian)
                    .message("Durian scan count incremented for: " + durianId);
        } else {
            return Result.ok().code(ResultCode.NOT_FOUND).message("Durian not found: " + durianId);
        }
    }

}
