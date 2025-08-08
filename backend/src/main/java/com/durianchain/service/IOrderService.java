package com.durianchain.service;

import com.durianchain.dto.OrderDTO;

public interface IOrderService {

    boolean placeOrder(OrderDTO dto);

    boolean cancelOrder(OrderDTO dto);

}
