package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
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
                          @RequestParam Integer pageSize) {
        QueryWrapper<Batch> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<Batch> page = batchService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged Batch List");
    }
}
