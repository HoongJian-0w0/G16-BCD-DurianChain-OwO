package com.durianchain.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginVO {
    private String token;
    private Long id;
    private String username;
    private String name;
    private String role;
    private String wallet;
}