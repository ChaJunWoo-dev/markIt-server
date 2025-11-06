package com.cha.markit.service;

import com.cha.markit.dto.config.WatermarkConfig;
import com.cha.markit.dto.response.WatermarkProcessResponse;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface WatermarkService {

    BufferedImage applyWatermark(MultipartFile image, WatermarkConfig config) throws IOException;

    byte[] createWatermarkZip(List<MultipartFile> images, WatermarkConfig config) throws IOException;

    WatermarkProcessResponse saveWatermark(String userId, byte[] zipData, int imageCount) throws IOException;
}
