package com.cha.markit.service;

import com.cha.markit.dto.config.WatermarkConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class WatermarkServiceImpl implements WatermarkService {

    @Override
    public BufferedImage applyWatermark(MultipartFile image, WatermarkConfig config) throws IOException {
        return null;
    }

    @Override
    public byte[] createZip(List<BufferedImage> processedImages, List<String> filenames) throws IOException {
        return new byte[0];
    }
}
