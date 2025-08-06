package com.durianchain.service.impl;

import com.durianchain.entity.User;
import com.durianchain.mapper.UserMapper;
import com.durianchain.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  Service Implementation Class
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
