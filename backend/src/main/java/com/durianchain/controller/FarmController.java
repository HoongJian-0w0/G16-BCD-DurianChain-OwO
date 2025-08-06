package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.durianchain.service.IFarmService;
import com.durianchain.entity.Farm;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController
@RequestMapping("/farm")
public class FarmController {

    @Resource
    private IFarmService farmService;

    @GetMapping
    public Result getAllFarm() {
        return Result.ok().data("list", farmService.list()).message("Fetched All Farm");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.ok().data("item", farmService.getById(id)).message("Fetched Farm by ID");
    }

    @PostMapping
    public Result save(@RequestBody Farm farm) {
        try {
            farmService.save(farm);
            return Result.ok().message("Farm Saved Successfully");
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to Save Farm");
        }
    }

    @PutMapping
    public Result update(@RequestBody Farm farm) {
        if (farmService.updateById(farm)) {
            return Result.ok().message("Farm Updated Successfully");
        }
        throw new ServiceException(ResultCode.NOT_FOUND, "Update Failed: Farm Not Found");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (farmService.removeById(id)) {
            return Result.ok().message("Farm Deleted Successfully");
        }
        return Result.error().message("Failed to Delete Farm");
    }

    @DeleteMapping("/del/batch/{ids}")
    public Result deleteBatch(@PathVariable List<Integer> ids) {
        if (farmService.removeByIds(ids)) {
            return Result.ok().message("Farms Deleted Successfully");
        }
        return Result.error().message("Failed to Batch Delete Farms");
    }

    @GetMapping("/page")
    public Result getPage(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize
    ) {

        QueryWrapper<Farm> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<Farm> page = farmService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged Farm List");
    }
}
