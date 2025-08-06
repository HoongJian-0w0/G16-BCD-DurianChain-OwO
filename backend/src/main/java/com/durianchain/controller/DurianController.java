package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

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
                          @RequestParam Integer pageSize) {
        QueryWrapper<Durian> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<Durian> page = durianService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged Durian List");
    }
}
