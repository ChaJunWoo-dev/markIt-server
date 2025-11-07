package com.cha.markit.service;

import com.cha.markit.dto.config.WatermarkConfig;
import com.cha.markit.dto.response.WatermarkResponse;
import com.cha.markit.repository.WatermarkRepository;
import com.cha.markit.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WatermarkServiceImpl implements WatermarkService {
    private final WatermarkRepository watermarkRepository;
    private final S3Service s3Service;
    private final WatermarkProcessor watermarkProcessor;
    private final WatermarkStorage watermarkStorage;

    @Override
    public byte[] createWatermarkZip(List<MultipartFile> images, WatermarkConfig config) throws IOException {
        return watermarkProcessor.createWatermarkZip(images, config);
    }

    @Override
    public WatermarkResponse saveWatermark(String userId, byte[] zipData, int imageCount) throws IOException {
        return watermarkStorage.save(userId, zipData, imageCount);
    }

    @Override
    public BufferedImage applyWatermark(MultipartFile image, WatermarkConfig config) throws IOException {
        return watermarkProcessor.applyWatermark(image, config);
    }
}
