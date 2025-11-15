package com.cha.markit.service;

import com.cha.markit.dto.config.ImageWatermarkConfig;
import com.cha.markit.dto.config.TextWatermarkConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Component
public class WatermarkGraphicsHelper {

    public BufferedImage readImage(MultipartFile file) throws IOException {
        try (var inputStream = file.getInputStream()) {
            BufferedImage image = javax.imageio.ImageIO.read(inputStream);

            if (image == null) {
                throw new IOException("이미지를 읽을 수 없습니다: " + file.getOriginalFilename());
            }

            return image;
        }
    }

    public void applyTextWatermark(BufferedImage image, TextWatermarkConfig config) {
        Graphics2D graphics = image.createGraphics();

        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, config.getOpacity()));
        graphics.setFont(new Font("Arial", Font.BOLD, config.getFontSize()));
        graphics.setColor(hexToColor(config.getColor()));

        Point position = calculatePosition(image, config.getPosition(), getTextBounds(graphics, config.getText()));
        graphics.drawString(config.getText(), position.x, position.y);

        graphics.dispose();
    }

    public void applyImageWatermark(BufferedImage image, ImageWatermarkConfig config) throws IOException {
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

    public BufferedImage scaleImage(BufferedImage original, int targetWidth) {
        //비율 유지
        int targetHeight = (int) (original.getHeight() * ((double) targetWidth / original.getWidth()));
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(original, 0, 0, targetWidth, targetHeight, null);

        graphics.dispose();

        return scaled;
    }

    public Point calculatePosition(BufferedImage image, String position, Dimension watermarkSize) {
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

    public Color hexToColor(String hex) {
        return new Color(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        );
    }

    public Dimension getTextBounds(Graphics2D graphics, String text) {
        FontMetrics metrics = graphics.getFontMetrics();

        return new Dimension(metrics.stringWidth(text), metrics.getHeight());
    }
}
