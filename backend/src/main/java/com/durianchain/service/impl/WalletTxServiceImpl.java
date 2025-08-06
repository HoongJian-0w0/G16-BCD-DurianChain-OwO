package com.durianchain.service.impl;

import com.durianchain.entity.WalletTx;
import com.durianchain.mapper.WalletTxMapper;
import com.durianchain.service.IWalletTxService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  Service Implementation Class
 */
@Service
public class WalletTxServiceImpl extends ServiceImpl<WalletTxMapper, WalletTx> implements IWalletTxService {

}
