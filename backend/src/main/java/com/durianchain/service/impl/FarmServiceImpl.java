package com.durianchain.service.impl;

import com.durianchain.entity.Farm;
import com.durianchain.mapper.FarmMapper;
import com.durianchain.service.IFarmService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  Service Implementation Class
 */
@Service
public class FarmServiceImpl extends ServiceImpl<FarmMapper, Farm> implements IFarmService {

}
