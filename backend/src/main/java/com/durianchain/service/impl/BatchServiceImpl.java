package com.durianchain.service.impl;

import com.durianchain.entity.Batch;
import com.durianchain.mapper.BatchMapper;
import com.durianchain.service.IBatchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  Service Implementation Class
 */
@Service
public class BatchServiceImpl extends ServiceImpl<BatchMapper, Batch> implements IBatchService {

}
