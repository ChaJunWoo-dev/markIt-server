package com.cha.markit.service;

import com.cha.markit.dto.config.ImageWatermarkConfig;
import com.cha.markit.dto.config.TextWatermarkConfig;
import com.cha.markit.dto.config.WatermarkConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatermarkProcessor {

    private final WatermarkGraphicsHelper watermarkGraphicsHelper;

    public byte[] createWatermarkZip(List<MultipartFile> images, WatermarkConfig config) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (MultipartFile imageFile : images) {
                BufferedImage watermarked = applyWatermark(imageFile, config);
                ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
                ImageIO.write(watermarked, "png", imageOut);
                String filename = imageFile.getOriginalFilename() == null ? "watermarked.png" : imageFile.getOriginalFilename();
                zos.putNextEntry(new ZipEntry(filename));
                zos.write(imageOut.toByteArray());
                zos.closeEntry();
            }
        }

        return baos.toByteArray();
    }

    public BufferedImage applyWatermark(MultipartFile image, WatermarkConfig config) throws IOException {
        BufferedImage base = watermarkGraphicsHelper.readImage(image);
        if (config instanceof TextWatermarkConfig textConfig)
            watermarkGraphicsHelper.applyTextWatermark(base, textConfig);
        else if (config instanceof ImageWatermarkConfig imgConfig)
            watermarkGraphicsHelper.applyImageWatermark(base, imgConfig);

        return base;
    }
}

