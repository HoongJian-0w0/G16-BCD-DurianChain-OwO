package com.durianchain.controller;

import com.durianchain.common.result.Result;
import com.durianchain.dto.ParcelDTO;
import com.durianchain.service.IParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parcel")
public class ParcelController {

    @Autowired
    private IParcelService parcelService;

    @PostMapping("/collect")
    public Result collectParcel(@RequestBody ParcelDTO dto) {
        parcelService.collectParcel(dto);
        return Result.ok().message("Parcel collected successfully.");
    }

    @PostMapping("/deliver")
    public Result deliverParcel(@RequestBody ParcelDTO dto) {
        parcelService.deliverParcel(dto);
        return Result.ok().message("Parcel delivery confirmed.");
    }
}