package com.durianchain.controller;

import com.durianchain.common.result.Result;
import com.durianchain.dto.LoginDTO;
import com.durianchain.dto.RegisterDTO;
import com.durianchain.service.IAuthService;
import com.durianchain.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        authService.registerUser(registerDTO);
        return Result.ok().message("User registered successfully");
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.authenticate(loginDTO);
        return Result.ok().message("Login successful").data("user", loginVO);
    }

}
