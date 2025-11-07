package com.cha.markit.service;

import com.cha.markit.domain.Watermark;
import com.cha.markit.dto.config.ImageWatermarkConfig;
import com.cha.markit.dto.config.TextWatermarkConfig;
import com.cha.markit.dto.config.WatermarkConfig;
import com.cha.markit.dto.response.WatermarkResponse;
import com.cha.markit.repository.WatermarkRepository;
import com.cha.markit.s3.S3Service;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatermarkServiceImpl implements WatermarkService {

    private final WatermarkRepository watermarkRepository;
    private final S3Service s3Service;

    @Override
    public byte[] createWatermarkZip(List<MultipartFile> images, WatermarkConfig config) throws IOException {
        log.info("워터마크 ZIP 생성 시작 - 이미지 개수: {}", images.size());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (MultipartFile imageFile : images) {
                BufferedImage watermarked = applyWatermark(imageFile, config);

                ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
                ImageIO.write(watermarked, "png", imageOut);

                String filename = imageFile.getOriginalFilename();
                if (filename == null || filename.isEmpty()) {
                    filename = "watermarked.png";
                }

                zos.putNextEntry(new ZipEntry(filename));
                zos.write(imageOut.toByteArray());
                zos.closeEntry();
            }
        }

        log.info("워터마크 ZIP 생성 완료");

        return baos.toByteArray();
    }

    @Override
    public WatermarkResponse saveWatermark(String userId, byte[] zipData, int imageCount) throws IOException {
        log.info("워터마크 저장 시작 - userId: {}", userId);

        String key = UUID.randomUUID() + ".zip";

        String eTag = uploadS3Watermark(zipData, key);
        Watermark watermark = saveWatermarkMetadata(userId, imageCount, key, eTag);

        return WatermarkResponse.builder()
                .id(key)
                .imageCount(imageCount)
                .createdAt(watermark.getCreatedAt())
                .build();
    }

    private String uploadS3Watermark(byte[] zipData, String key) throws IOException {
        CompletableFuture<String> eTagFuture = s3Service.uploadBytesAsync(key, zipData);

        try {
            return eTagFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("S3 업로드 중 오류 발생", e);
        }
    }

    private Watermark saveWatermarkMetadata(String userId, int imageCount, String key, String eTag) {
        Watermark watermark = Watermark.builder()
                .id(key)
                .userId(userId)
                .key(key)
                .eTag(eTag)
                .imageCount(imageCount)
                .build();

        watermarkRepository.save(watermark);

        log.info("워터마크 메타데이터 저장 완료 - key: {}", key);

        return watermark;
    }

    @Override
    public BufferedImage applyWatermark(MultipartFile image, WatermarkConfig config) throws IOException {
        BufferedImage bufferedImage = readImage(image);
        
        if (config instanceof TextWatermarkConfig textConfig) {
            applyTextWatermark(bufferedImage, textConfig);
        } else if (config instanceof ImageWatermarkConfig imageConfig) {
            applyImageWatermark(bufferedImage, imageConfig);
        }
        
        return bufferedImage;
    }

    private BufferedImage readImage(MultipartFile file) throws IOException {
        try (var inputStream = file.getInputStream()) {
            BufferedImage image = javax.imageio.ImageIO.read(inputStream);

            if (image == null) {
                throw new IOException("이미지를 읽을 수 없습니다: " + file.getOriginalFilename());
            }

            return image;
        }
    }

    private void applyTextWatermark(BufferedImage image, TextWatermarkConfig config) {
        Graphics2D graphics = image.createGraphics();
        
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, config.getOpacity()));
        graphics.setFont(new Font("Arial", Font.BOLD, config.getFontSize()));
        graphics.setColor(hexToColor(config.getColor()));
        
        Point position = calculatePosition(image, config.getPosition(), getTextBounds(graphics, config.getText()));
        graphics.drawString(config.getText(), position.x, position.y);
        
        graphics.dispose();
    }

    private Color hexToColor(String hex) {
        return new Color(
            Integer.valueOf(hex.substring(1, 3), 16),
            Integer.valueOf(hex.substring(3, 5), 16),
            Integer.valueOf(hex.substring(5, 7), 16)
        );
    }

    private Dimension getTextBounds(Graphics2D graphics, String text) {
        FontMetrics metrics = graphics.getFontMetrics();

        return new Dimension(metrics.stringWidth(text), metrics.getHeight());
    }

    private void applyImageWatermark(BufferedImage image, ImageWatermarkConfig config) throws IOException {
        BufferedImage watermarkImage = readImage(config.getImage());
        BufferedImage scaledWatermark = scaleImage(watermarkImage, config.getWidth());

        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, config.getOpacity()));

        Point position = calculatePosition(image, config.getPosition(), new Dimension(
                scaledWatermark.getWidth(), scaledWatermark.getHeight()
        ));
        graphics.drawImage(scaledWatermark, position.x, position.y, null);

        graphics.dispose();
    }

    private BufferedImage scaleImage(BufferedImage original, int targetWidth) {
        //비율 유지
        int targetHeight = (int) (original.getHeight() * ((double) targetWidth / original.getWidth()));
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(original, 0, 0, targetWidth, targetHeight, null);

        graphics.dispose();

        return scaled;
    }

    private Point calculatePosition(BufferedImage image, String position, Dimension watermarkSize) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int wmWidth = watermarkSize.width;
        int wmHeight = watermarkSize.height;
        
        return switch (position) {
            case "TOP_LEFT" -> new Point(0, wmHeight);
            case "TOP_CENTER" -> new Point((imageWidth - wmWidth) / 2, wmHeight);
            case "TOP_RIGHT" -> new Point(imageWidth - wmWidth, wmHeight);
            case "CENTER_LEFT" -> new Point(0, (imageHeight + wmHeight) / 2);
            case "CENTER" -> new Point((imageWidth - wmWidth) / 2, (imageHeight + wmHeight) / 2);
            case "CENTER_RIGHT" -> new Point(imageWidth - wmWidth, (imageHeight + wmHeight) / 2);
            case "BOTTOM_LEFT" -> new Point(0, imageHeight);
            case "BOTTOM_CENTER" -> new Point((imageWidth - wmWidth) / 2, imageHeight);
            case "BOTTOM_RIGHT" -> new Point(imageWidth - wmWidth, imageHeight);
            default -> new Point(0, 0);
        };
    }
}
