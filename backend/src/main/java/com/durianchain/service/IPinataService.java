package com.durianchain.service;

import org.springframework.web.multipart.MultipartFile;

public interface IPinataService {
    String uploadFileToIPFS(MultipartFile file);
}