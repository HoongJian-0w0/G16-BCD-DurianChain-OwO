package com.durianchain.service;

import com.durianchain.dto.LoginDTO;
import com.durianchain.dto.RegisterDTO;
import com.durianchain.vo.LoginVO;

public interface IAuthService {

    boolean registerUser(RegisterDTO registerDTO);

    LoginVO authenticate(LoginDTO loginDTO);
}
