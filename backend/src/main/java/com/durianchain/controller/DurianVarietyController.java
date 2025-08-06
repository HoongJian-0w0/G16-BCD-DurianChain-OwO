package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.durianchain.service.IDurianVarietyService;
import com.durianchain.entity.DurianVariety;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController
@RequestMapping("/durian-variety")
public class DurianVarietyController {

    @Resource
    private IDurianVarietyService durianVarietyService;

    @GetMapping
    public Result getAllDurianVariety() {
        return Result.ok().data("list", durianVarietyService.list()).message("Fetched All DurianVariety");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.ok().data("item", durianVarietyService.getById(id)).message("Fetched DurianVariety by ID");
    }

    @PostMapping
    public Result save(@RequestBody DurianVariety durianVariety) {
        try {
                durianVarietyService.save(durianVariety);
            return Result.ok().message("DurianVariety Saved Successfully");
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to Save DurianVariety");
        }
    }

    @PutMapping
    public Result update(@RequestBody DurianVariety durianVariety) {
        if (durianVarietyService.updateById(durianVariety)) {
            return Result.ok().message("DurianVariety Updated Successfully");
        }
        throw new ServiceException(ResultCode.NOT_FOUND, "Update Failed: DurianVariety Not Found");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (durianVarietyService.removeById(id)) {
            return Result.ok().message("DurianVariety Deleted Successfully");
        }
        return Result.error().message("Failed to Delete DurianVariety");
    }

    @DeleteMapping("/del/batch/{ids}")
    public Result deleteBatch(@PathVariable List<Integer> ids) {
        if (durianVarietyService.removeByIds(ids)) {
            return Result.ok().message("DurianVarietys Deleted Successfully");
        }
        return Result.error().message("Failed to Batch Delete DurianVarietys");
    }

    @GetMapping("/page")
    public Result getPage(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize) {
        QueryWrapper<DurianVariety> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<DurianVariety> page = durianVarietyService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged DurianVariety List");
    }
}
