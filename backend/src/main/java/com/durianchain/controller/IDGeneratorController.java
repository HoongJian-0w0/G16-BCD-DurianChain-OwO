package com.durianchain.controller;

import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;
import com.durianchain.service.IDGeneratorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/util")
public class IDGeneratorController {

    @Resource
    private IDGeneratorService idGeneratorService;

    @GetMapping("/genId")
    public Result generateId(@RequestParam String type) {
        try {
            String generatedId = idGeneratorService.generateId(type);
            return Result.ok().data("generatedId", generatedId).message("ID Generated Successfully");
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ResultCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to generate ID");
        }
    }
}