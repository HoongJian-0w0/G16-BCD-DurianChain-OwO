package com.durianchain.controller;

import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import com.durianchain.dto.OrderDTO;
import com.durianchain.exception.ServiceException;
import com.durianchain.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/place")
    public Result placeOrder(@RequestBody OrderDTO dto) {
        orderService.placeOrder(dto);
        return Result.ok().message("File uploaded to IPFS successfully.");
    }

    @PostMapping("/cancel")
    public Result cancelOrder(@RequestBody OrderDTO dto) {
        orderService.cancelOrder(dto);
        return Result.ok().message("Order cancelled and updated in DB.");
    }

}