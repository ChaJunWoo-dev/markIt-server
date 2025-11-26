package com.cha.markit.service;

import com.cha.markit.dto.request.ImageWatermarkRequest;
import com.cha.markit.dto.request.TextWatermarkRequest;
import com.cha.markit.dto.request.WatermarkRequest;
import com.cha.markit.exception.BusinessException;
import com.cha.markit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
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

    private static final int THUMBNAIL_SIZE = 200;

    private final WatermarkGraphicsHelper watermarkGraphicsHelper;

    public byte[] createWatermarkZip(List<MultipartFile> images, TextWatermarkRequest request) throws IOException {
        return createZip(images, request);
    }

    public byte[] createWatermarkZip(List<MultipartFile> images, ImageWatermarkRequest request) throws IOException {
        return createZip(images, request);
    }

    private byte[] createZip(List<MultipartFile> images, WatermarkRequest request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        if (images.size() >= 5) {
            createZipParallel(images, request, baos);
        } else {
            createZipSequential(images, request, baos);
        }
        
        return baos.toByteArray();
    }

    private void createZipSequential(List<MultipartFile> images, WatermarkRequest request, ByteArrayOutputStream baos) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (MultipartFile imageFile : images) {
                processAndAddToZip(imageFile, request, zos);
            }
        }
    }

    private void createZipParallel(List<MultipartFile> images, WatermarkRequest request, ByteArrayOutputStream baos) throws IOException {
        List<ProcessedImage> processedImages = images.parallelStream()
                .map(imageFile -> {
                    try {
                        BufferedImage watermarked = applyWatermark(imageFile, request);
                        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
                        ImageIO.write(watermarked, "png", imageOut);
                        String filename = imageFile.getOriginalFilename() == null ? "watermarked.png" : imageFile.getOriginalFilename();
                        return new ProcessedImage(filename, imageOut.toByteArray());
                    } catch (IOException e) {
                        throw new BusinessException(ErrorCode.IMAGE_PROCESSING_FAILED, e);
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

    private void processAndAddToZip(MultipartFile imageFile, WatermarkRequest request, ZipOutputStream zos) throws IOException {
        BufferedImage watermarked = applyWatermark(imageFile, request);
        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
        ImageIO.write(watermarked, "png", imageOut);
        String filename = imageFile.getOriginalFilename() == null ? "watermarked.png" : imageFile.getOriginalFilename();
        zos.putNextEntry(new ZipEntry(filename));
        zos.write(imageOut.toByteArray());
        zos.closeEntry();
    }

    private record ProcessedImage(String filename, byte[] data) {
    }

    public BufferedImage applyWatermark(MultipartFile image, TextWatermarkRequest request) throws IOException {
        return applyWatermark(image, (WatermarkRequest) request);
    }

    public BufferedImage applyWatermark(MultipartFile image, ImageWatermarkRequest request) throws IOException {
        return applyWatermark(image, (WatermarkRequest) request);
    }

    private BufferedImage applyWatermark(MultipartFile image, WatermarkRequest request) throws IOException {
        BufferedImage base = watermarkGraphicsHelper.readImage(image);
        
        if (request instanceof TextWatermarkRequest textReq) {
            watermarkGraphicsHelper.applyTextWatermark(base, textReq);
        } else if (request instanceof ImageWatermarkRequest imgReq) {
            watermarkGraphicsHelper.applyImageWatermark(base, imgReq);
        }

        return base;
    }

    public byte[] createThumbnail(List<MultipartFile> images, WatermarkRequest request) throws IOException {
        if (images.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_IMAGE_LIST);
        }

        BufferedImage watermarkedImage = applyWatermark(images.get(0), request);
        BufferedImage thumbnail = resizeToThumbnail(watermarkedImage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "jpg", baos);
        return baos.toByteArray();
    }

    private BufferedImage resizeToThumbnail(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        double scale = Math.min((double) THUMBNAIL_SIZE / width, (double) THUMBNAIL_SIZE / height);
        int targetWidth = (int) (width * scale);
        int targetHeight = (int) (height * scale);

        BufferedImage thumbnail = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = thumbnail.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();

        return thumbnail;
    }
}

