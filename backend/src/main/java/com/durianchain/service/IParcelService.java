package com.durianchain.service;

import com.durianchain.dto.ParcelDTO;

public interface IParcelService {
    boolean collectParcel(ParcelDTO dto);
    boolean deliverParcel(ParcelDTO dto);
}
