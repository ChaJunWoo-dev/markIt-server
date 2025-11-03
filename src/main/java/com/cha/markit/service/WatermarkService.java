package com.cha.markit.service;

import com.cha.markit.dto.config.WatermarkConfig;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface WatermarkService {

    BufferedImage applyWatermark(MultipartFile image, WatermarkConfig config) throws IOException;

    byte[] createZip(List<BufferedImage> processedImages, List<String> filenames) throws IOException;
}
