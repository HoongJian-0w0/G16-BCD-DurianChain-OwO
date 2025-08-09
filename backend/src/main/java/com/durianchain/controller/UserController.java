package com.durianchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.durianchain.service.IUserService;
import com.durianchain.entity.User;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @GetMapping
    public Result getAllUser() {
        return Result.ok().data("list", userService.list()).message("Fetched All User");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.ok().data("item", userService.getById(id)).message("Fetched User by ID");
    }

    @PostMapping
    public Result save(@RequestBody User user) {
        try {
                userService.save(user);
            return Result.ok().message("User Saved Successfully");
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to Save User");
        }
    }

    @PutMapping
    public Result update(@RequestBody User user) {
        if (userService.updateById(user)) {
            return Result.ok().message("User Updated Successfully");
        }
        throw new ServiceException(ResultCode.NOT_FOUND, "Update Failed: User Not Found");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (userService.removeById(id)) {
            return Result.ok().message("User Deleted Successfully");
        }
        return Result.error().message("Failed to Delete User");
    }

    @DeleteMapping("/del/batch/{ids}")
    public Result deleteBatch(@PathVariable List<Integer> ids) {
        if (userService.removeByIds(ids)) {
            return Result.ok().message("Users Deleted Successfully");
        }
        return Result.error().message("Failed to Batch Delete Users");
    }

    @GetMapping("/page")
    public Result getPage(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String email) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // Optional filters
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (email != null && !email.isEmpty()) {
            queryWrapper.like("email", email);
        }

        queryWrapper.orderByDesc("id");

        // Paginated query
        Page<User> page = userService.page(new Page<>(pageNum, pageSize), queryWrapper);

        // Role counts
        long adminCount = userService.count(new QueryWrapper<User>().eq("role", "admin"));
        long farmerCount = userService.count(new QueryWrapper<User>().eq("role", "farmer"));
        long logisticsCount = userService.count(new QueryWrapper<User>().eq("role", "logistics"));
        long traderCount = userService.count(new QueryWrapper<User>().eq("role", "trader"));

        // Return combined result
        return Result.ok()
                .data("page", page)
                .data("stats", Map.of(
                        "admin", adminCount,
                        "farmer", farmerCount,
                        "logistics", logisticsCount,
                        "trader", traderCount
                ))
                .message("Paged User List with Role Stats");
    }

}
