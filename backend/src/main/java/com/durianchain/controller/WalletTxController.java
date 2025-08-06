package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.durianchain.service.IWalletTxService;
import com.durianchain.entity.WalletTx;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController
@RequestMapping("/wallet-tx")
public class WalletTxController {

    @Resource
    private IWalletTxService walletTxService;

    @GetMapping
    public Result getAllWalletTx() {
        return Result.ok().data("list", walletTxService.list()).message("Fetched All WalletTx");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.ok().data("item", walletTxService.getById(id)).message("Fetched WalletTx by ID");
    }

    @PostMapping
    public Result save(@RequestBody WalletTx walletTx) {
        try {
                walletTxService.save(walletTx);
            return Result.ok().message("WalletTx Saved Successfully");
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to Save WalletTx");
        }
    }

    @PutMapping
    public Result update(@RequestBody WalletTx walletTx) {
        if (walletTxService.updateById(walletTx)) {
            return Result.ok().message("WalletTx Updated Successfully");
        }
        throw new ServiceException(ResultCode.NOT_FOUND, "Update Failed: WalletTx Not Found");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (walletTxService.removeById(id)) {
            return Result.ok().message("WalletTx Deleted Successfully");
        }
        return Result.error().message("Failed to Delete WalletTx");
    }

    @DeleteMapping("/del/batch/{ids}")
    public Result deleteBatch(@PathVariable List<Integer> ids) {
        if (walletTxService.removeByIds(ids)) {
            return Result.ok().message("WalletTxs Deleted Successfully");
        }
        return Result.error().message("Failed to Batch Delete WalletTxs");
    }

    @GetMapping("/page")
    public Result getPage(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize) {
        QueryWrapper<WalletTx> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<WalletTx> page = walletTxService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.ok().data("page", page).message("Paged WalletTx List");
    }
}
