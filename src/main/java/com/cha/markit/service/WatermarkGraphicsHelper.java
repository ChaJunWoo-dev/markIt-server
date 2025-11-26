package com.cha.markit.service;

import com.cha.markit.dto.request.ImageWatermarkRequest;
import com.cha.markit.dto.request.TextWatermarkRequest;
import com.cha.markit.exception.BusinessException;
import com.cha.markit.exception.ErrorCode;
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
                throw new BusinessException(ErrorCode.IMAGE_READ_FAILED);
            }

            return image;
        }
    }

    public void applyTextWatermark(BufferedImage image, TextWatermarkRequest request) {
        Graphics2D graphics = image.createGraphics();

        int fontSize = (int) (image.getHeight() * request.getSize() / 100.0);

        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, request.getOpacity()));
        graphics.setFont(new Font("Arial", Font.BOLD, fontSize));
        graphics.setColor(hexToColor(request.getColor()));

        FontMetrics metrics = graphics.getFontMetrics();
        Dimension textBounds = getTextBounds(graphics, request.getText());
        Point position = calculateTextPosition(image, request.getPosition(), textBounds, metrics);
        graphics.drawString(request.getText(), position.x, position.y);

        graphics.dispose();
    }

    public void applyImageWatermark(BufferedImage image, ImageWatermarkRequest request) throws IOException {
        BufferedImage watermarkImage = readImage(request.getWatermarkImage());

        int width = (int) (image.getWidth() * request.getSize() / 100.0);
        BufferedImage scaledWatermark = scaleImage(watermarkImage, width);

        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, request.getOpacity()));

        Point position = calculateImagePosition(image, request.getPosition(), new Dimension(
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

    public Point calculateTextPosition(BufferedImage image, String position, Dimension textBounds, FontMetrics metrics) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int textWidth = textBounds.width;
        int textHeight = metrics.getHeight();
        int ascent = metrics.getAscent();

        return switch (position) {
            case "TOP_LEFT" -> new Point(0, ascent);
            case "TOP_CENTER" -> new Point((imageWidth - textWidth) / 2, ascent);
            case "TOP_RIGHT" -> new Point(imageWidth - textWidth, ascent);
            case "CENTER_LEFT" -> new Point(0, (imageHeight + ascent) / 2);
            case "CENTER" -> new Point((imageWidth - textWidth) / 2, (imageHeight + ascent) / 2);
            case "CENTER_RIGHT" -> new Point(imageWidth - textWidth, (imageHeight + ascent) / 2);
            case "BOTTOM_LEFT" -> new Point(0, imageHeight - textHeight + ascent);
            case "BOTTOM_CENTER" -> new Point((imageWidth - textWidth) / 2, imageHeight - textHeight + ascent);
            case "BOTTOM_RIGHT" -> new Point(imageWidth - textWidth, imageHeight - textHeight + ascent);
            default -> new Point(0, ascent);
        };
    }

    public Point calculateImagePosition(BufferedImage image, String position, Dimension watermarkSize) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int wmWidth = watermarkSize.width;
        int wmHeight = watermarkSize.height;

        return switch (position) {
            case "TOP_LEFT" -> new Point(0, 0);
            case "TOP_CENTER" -> new Point((imageWidth - wmWidth) / 2, 0);
            case "TOP_RIGHT" -> new Point(imageWidth - wmWidth, 0);
            case "CENTER_LEFT" -> new Point(0, (imageHeight - wmHeight) / 2);
            case "CENTER" -> new Point((imageWidth - wmWidth) / 2, (imageHeight - wmHeight) / 2);
            case "CENTER_RIGHT" -> new Point(imageWidth - wmWidth, (imageHeight - wmHeight) / 2);
            case "BOTTOM_LEFT" -> new Point(0, imageHeight - wmHeight);
            case "BOTTOM_CENTER" -> new Point((imageWidth - wmWidth) / 2, imageHeight - wmHeight);
            case "BOTTOM_RIGHT" -> new Point(imageWidth - wmWidth, imageHeight - wmHeight);
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
