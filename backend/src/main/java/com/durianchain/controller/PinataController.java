package com.durianchain.controller;

import com.durianchain.common.result.Result;
import com.durianchain.service.IPinataService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ipfs")
public class PinataController {

    @Resource
    private IPinataService pinataService;

    @PostMapping("/upload")
    public Result uploadToIPFS(@RequestParam("file") MultipartFile file) {
        String cid = pinataService.uploadFileToIPFS(file);
        return Result.ok().data("cid", cid).message("File uploaded to IPFS successfully.");
    }
}
