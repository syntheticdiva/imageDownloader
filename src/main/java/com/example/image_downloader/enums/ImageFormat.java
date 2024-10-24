package com.example.image_downloader.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ImageFormat {
    JPG, JPEG, PNG, GIF, BMP, ALL;

    public static List<String> getExtensions(ImageFormat format) {
        switch (format) {
            case ALL:
                return Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
            case JPG:
            case JPEG:
                return Arrays.asList(".jpg", ".jpeg");
            case PNG:
                return Collections.singletonList(".png");
            case GIF:
                return Collections.singletonList(".gif");
            case BMP:
                return Collections.singletonList(".bmp");
            default:
                throw new IllegalArgumentException("Unsupported image format: " + format);
        }
    }

    public static boolean isValidExtension(String extension, ImageFormat format) {
        return format == ALL || getExtensions(format).contains(extension.toLowerCase());
    }
}