package com.durianchain.service.impl;

import com.durianchain.common.config.PinataConfig;
import com.durianchain.common.result.ResultCode;
import com.durianchain.exception.ServiceException;
import com.durianchain.service.IPinataService;
import jakarta.annotation.Resource;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class IPinataServiceImpl implements IPinataService {

    @Resource
    private PinataConfig pinataConfig;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String uploadFileToIPFS(MultipartFile file) {
        try {
            RequestBody fileBody = RequestBody.create(
                    file.getBytes(),
                    MediaType.parse("application/octet-stream")
            );

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getOriginalFilename(), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(pinataConfig.baseUrl + "/pinning/pinFileToIPFS")
                    .addHeader("Authorization", pinataConfig.jwt)
                    .addHeader("Accept", "application/json")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Upload failed: " + response.message());
                }

                return extractCidFromJson(response.body().string());
            }
        } catch (Exception e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Failed to upload to IPFS: " + e.getMessage());
        }
    }

    private String extractCidFromJson(String json) {
        int start = json.indexOf("\"IpfsHash\":\"") + 12;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
