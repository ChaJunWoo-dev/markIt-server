package com.cha.markit.service;

import com.cha.markit.dto.request.ImageWatermarkRequest;
import com.cha.markit.dto.request.TextWatermarkRequest;
import com.cha.markit.dto.response.WatermarkListResponse;
import com.cha.markit.dto.response.WatermarkResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public interface WatermarkService {

    byte[] createWatermarkZip(List<MultipartFile> images, TextWatermarkRequest request) throws IOException;
    byte[] createWatermarkZip(List<MultipartFile> images, ImageWatermarkRequest request) throws IOException;

    WatermarkResponse saveWatermark(String userId, byte[] zipData, byte[] thumbnailData, int imageCount) throws IOException;
    String getDownloadUrl(String watermarkKey, Duration expiration);
    List<WatermarkListResponse> getWatermarkList(String userId);

    void deleteWatermark(String watermarkKey, String userId);
}