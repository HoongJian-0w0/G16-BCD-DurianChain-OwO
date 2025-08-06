package com.durianchain.service.impl;

import com.durianchain.entity.Durian;
import com.durianchain.mapper.DurianMapper;
import com.durianchain.service.IDurianService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  Service Implementation Class
 */
@Service
public class DurianServiceImpl extends ServiceImpl<DurianMapper, Durian> implements IDurianService {

}
