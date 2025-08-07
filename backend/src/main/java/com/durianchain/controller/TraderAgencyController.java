package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.durianchain.service.ITraderAgencyService;
import com.durianchain.entity.TraderAgency;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController
@RequestMapping("/trader-agency")
public class TraderAgencyController {

    @Resource
    private ITraderAgencyService traderAgencyService;

    @GetMapping
    public Result getAllTraderAgency() {
        return Result.ok().data("list", traderAgencyService.list()).message("Fetched All TraderAgency");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.ok().data("item", traderAgencyService.getById(id)).message("Fetched TraderAgency by ID");
    }

    @PostMapping
    public Result save(@RequestBody TraderAgency traderAgency) {
        try {
                traderAgencyService.save(traderAgency);
            return Result.ok().message("TraderAgency Saved Successfully");
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to Save TraderAgency");
        }
    }

    @PutMapping
    public Result update(@RequestBody TraderAgency traderAgency) {
        if (traderAgencyService.updateById(traderAgency)) {
            return Result.ok().message("TraderAgency Updated Successfully");
        }
        throw new ServiceException(ResultCode.NOT_FOUND, "Update Failed: TraderAgency Not Found");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (traderAgencyService.removeById(id)) {
            return Result.ok().message("TraderAgency Deleted Successfully");
        }
        return Result.error().message("Failed to Delete TraderAgency");
    }

    @DeleteMapping("/del/batch/{ids}")
    public Result deleteBatch(@PathVariable List<Integer> ids) {
        if (traderAgencyService.removeByIds(ids)) {
            return Result.ok().message("TraderAgencys Deleted Successfully");
        }
        return Result.error().message("Failed to Batch Delete TraderAgencys");
    }

    @GetMapping("/page")
    public Result getPage(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String agencyId,
            @RequestParam(required = false) String agencyName,
            @RequestParam(required = false) String ownerAddress) {

        QueryWrapper<TraderAgency> queryWrapper = new QueryWrapper<>();

        if (agencyId != null && !agencyId.trim().isEmpty()) {
            queryWrapper.like("agency_id", agencyId.trim());
        }

        if (agencyName != null && !agencyName.trim().isEmpty()) {
            queryWrapper.like("agency_name", agencyName.trim());
        }

        if (ownerAddress != null && !ownerAddress.trim().isEmpty()) {
            queryWrapper.eq("owner_address", ownerAddress.trim());
        }

        queryWrapper.orderByDesc("id");

        Page<TraderAgency> page = traderAgencyService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged TraderAgency List");
    }

}
