package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.durianchain.service.ILogisticsCompanyService;
import com.durianchain.entity.LogisticsCompany;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController
@RequestMapping("/logistics-company")
public class LogisticsCompanyController {

    @Resource
    private ILogisticsCompanyService logisticsCompanyService;

    @GetMapping
    public Result getAllLogisticsCompany() {
        return Result.ok().data("list", logisticsCompanyService.list()).message("Fetched All LogisticsCompany");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.ok().data("item", logisticsCompanyService.getById(id)).message("Fetched LogisticsCompany by ID");
    }

    @PostMapping
    public Result save(@RequestBody LogisticsCompany logisticsCompany) {
        try {
                logisticsCompanyService.save(logisticsCompany);
            return Result.ok().message("LogisticsCompany Saved Successfully");
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to Save LogisticsCompany");
        }
    }

    @PutMapping
    public Result update(@RequestBody LogisticsCompany logisticsCompany) {
        if (logisticsCompanyService.updateById(logisticsCompany)) {
            return Result.ok().message("LogisticsCompany Updated Successfully");
        }
        throw new ServiceException(ResultCode.NOT_FOUND, "Update Failed: LogisticsCompany Not Found");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (logisticsCompanyService.removeById(id)) {
            return Result.ok().message("LogisticsCompany Deleted Successfully");
        }
        return Result.error().message("Failed to Delete LogisticsCompany");
    }

    @DeleteMapping("/del/batch/{ids}")
    public Result deleteBatch(@PathVariable List<Integer> ids) {
        if (logisticsCompanyService.removeByIds(ids)) {
            return Result.ok().message("LogisticsCompanys Deleted Successfully");
        }
        return Result.error().message("Failed to Batch Delete LogisticsCompanys");
    }

    @GetMapping("/page")
    public Result getPage(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String ownerAddress) {

        QueryWrapper<LogisticsCompany> queryWrapper = new QueryWrapper<>();

        if (companyId != null && !companyId.trim().isEmpty()) {
            queryWrapper.like("company_id", companyId.trim());
        }

        if (companyName != null && !companyName.trim().isEmpty()) {
            queryWrapper.like("company_name", companyName.trim());
        }

        if (ownerAddress != null && !ownerAddress.trim().isEmpty()) {
            queryWrapper.eq("owner_address", ownerAddress.trim());
        }

        queryWrapper.orderByDesc("id");

        Page<LogisticsCompany> page = logisticsCompanyService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged LogisticsCompany List");
    }
}
