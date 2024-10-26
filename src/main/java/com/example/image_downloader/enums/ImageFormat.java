package com.example.image_downloader.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ImageFormat {
    JPG, JPEG, PNG, GIF, BMP, ALL;

    public static List<String> getExtensions(ImageFormat format) {
        return switch (format) {
            case ALL -> Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
            case JPG, JPEG -> Arrays.asList(".jpg", ".jpeg");
            case PNG -> Collections.singletonList(".png");
            case GIF -> Collections.singletonList(".gif");
            case BMP -> Collections.singletonList(".bmp");
            default -> throw new IllegalArgumentException("Unsupported image format: " + format);
        };
    }

    public static boolean isValidExtension(String extension, ImageFormat format) {
        return format == ALL || getExtensions(format).contains(extension.toLowerCase());
    }
}