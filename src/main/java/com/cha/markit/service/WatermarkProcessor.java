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
        
        if (images.size() >= 5) {
            createZipParallel(images, config, baos);
        } else {
            createZipSequential(images, config, baos);
        }
        
        return baos.toByteArray();
    }

    private void createZipSequential(List<MultipartFile> images, WatermarkConfig config, ByteArrayOutputStream baos) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (MultipartFile imageFile : images) {
                processAndAddToZip(imageFile, config, zos);
            }
        }
    }

    private void createZipParallel(List<MultipartFile> images, WatermarkConfig config, ByteArrayOutputStream baos) throws IOException {
        List<ProcessedImage> processedImages = images.parallelStream()
                .map(imageFile -> {
                    try {
                        BufferedImage watermarked = applyWatermark(imageFile, config);
                        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
                        ImageIO.write(watermarked, "png", imageOut);
                        String filename = imageFile.getOriginalFilename() == null ? "watermarked.png" : imageFile.getOriginalFilename();
                        return new ProcessedImage(filename, imageOut.toByteArray());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (ProcessedImage img : processedImages) {
                zos.putNextEntry(new ZipEntry(img.filename));
                zos.write(img.data);
                zos.closeEntry();
            }
        }
    }

    private void processAndAddToZip(MultipartFile imageFile, WatermarkConfig config, ZipOutputStream zos) throws IOException {
        BufferedImage watermarked = applyWatermark(imageFile, config);
        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
        ImageIO.write(watermarked, "png", imageOut);
        String filename = imageFile.getOriginalFilename() == null ? "watermarked.png" : imageFile.getOriginalFilename();
        zos.putNextEntry(new ZipEntry(filename));
        zos.write(imageOut.toByteArray());
        zos.closeEntry();
    }

    private record ProcessedImage(String filename, byte[] data) {
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

