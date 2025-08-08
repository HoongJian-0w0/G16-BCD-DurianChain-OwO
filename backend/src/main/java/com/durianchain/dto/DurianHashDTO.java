package com.durianchain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DurianHashDTO {
    private String durianId;
    private String imageHash;
}