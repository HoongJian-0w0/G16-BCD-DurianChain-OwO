package com.durianchain.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.durianchain.common.util.JwtUtil;
import com.durianchain.dto.LoginDTO;
import com.durianchain.dto.RegisterDTO;
import com.durianchain.exception.ServiceException;
import com.durianchain.service.IAuthService;
import com.durianchain.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IAuthServiceImpl implements IAuthService {

    // @Autowired
    // private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean registerUser(RegisterDTO dto) {
      /*  if ("admin".equalsIgnoreCase(dto.getRole())) {
            throw new ServiceException(403, "You are not allowed to register as admin");
        }

        QueryWrapper<User> existingUserWrapper = new QueryWrapper<>();
        existingUserWrapper.eq("username", dto.getUsername())
                .or().eq("email", dto.getEmail());

        if (userMapper.selectCount(existingUserWrapper) > 0) {
            throw new ServiceException(409, "Username or email already exists");
        }

        if (dto.getWalletAddress() != null && !dto.getWalletAddress().isBlank()) {
            QueryWrapper<User> walletWrapper = new QueryWrapper<>();
            walletWrapper.eq("wallet_address", dto.getWalletAddress());
            Long count = userMapper.selectCount(walletWrapper);
            if (count > 0) {
                throw new ServiceException(409, "Wallet address already registered");
            }
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setName(dto.getName());
        user.setRole(dto.getRole());
        user.setWalletAddress(dto.getWalletAddress());

        boolean inserted = userMapper.insert(user) > 0;
        if (!inserted) {
            throw new ServiceException(500, "Failed to register user");
        }

        return true;*/
        return true;
    }

    @Override
    public LoginVO authenticate(LoginDTO loginDTO) {
       /* User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", loginDTO.getUsername()));
        if (user == null) {
            throw new ServiceException(404, "User not found");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new ServiceException(401, "Invalid password");
        }

        String wallet = loginDTO.getWallet();
        if (wallet != null && !wallet.isEmpty() && !wallet.equalsIgnoreCase(user.getWalletAddress())) {
            throw new ServiceException(403, "Wallet address mismatch");
        }

        if (Boolean.FALSE.equals(user.getIsApproved())) {
            throw new ServiceException(403, "Account not approved by admin");
        }

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", user.getId());
        claimsMap.put("username", user.getUsername());
        claimsMap.put("role", user.getRole());

        String subject = JSON.toJSONString(claimsMap);
        String token = JwtUtil.createJWT(subject, JwtUtil.JWT_TTL);

        System.out.println("farmIds: " + user.getFarmIds());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        vo.setFarmIds(user.getFarmIds());
        vo.setLogisticsCompanyIds(user.getLogisticsCompanyIds());
        vo.setTradingAgencyIds(user.getTradingAgencyIds());

        return vo;*/
        return null;
    }

}
