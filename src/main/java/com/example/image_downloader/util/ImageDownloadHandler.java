package com.example.image_downloader.util;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.service.impl.ImageDownloadServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ImageDownloadHandler {
    private final ImageDownloadServiceImpl downloadService;

    @Autowired
    public ImageDownloadHandler(ImageDownloadServiceImpl downloadService) {
        this.downloadService = downloadService;
    }

    public DownloadResult processDownloadRequest(String url, String savePath, List<String> formats) {
        log.debug("Received formats from form: {}", formats);
        validateUrl(url);
        List<ImageFormat> selectedFormats = getSelectedFormats(formats);
        log.info("Processing download request for formats: {}", selectedFormats);
        return downloadService.downloadImages(url, savePath, selectedFormats);
    }

    public void validateUrl(String url) {
        if (!isValidUrl(url)) {
            throw new IllegalArgumentException("Неверный URL: " + url);
        }
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private List<ImageFormat> getSelectedFormats(List<String> formats) {
        if (formats != null && formats.contains("ALL")) {
            return Collections.singletonList(ImageFormat.ALL);
        } else if (formats != null && !formats.isEmpty()) {
            return formats.stream()
                    .map(ImageFormat::valueOf)
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(ImageFormat.ALL);
        }
    }
}
