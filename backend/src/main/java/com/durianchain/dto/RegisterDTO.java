package com.durianchain.dto;

import lombok.Data;

@Data
public class RegisterDTO {

    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
    private String role;
    private String walletAddress;

}
