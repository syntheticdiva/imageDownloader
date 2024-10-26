package com.example.image_downloader.controller;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.service.impl.ImageDownloadServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class ImageDownloadWebController {

    private final ImageDownloadServiceImpl downloadService;

    @Autowired
    public ImageDownloadWebController(ImageDownloadServiceImpl downloadService) {
        this.downloadService = downloadService;
    }

    @GetMapping("/")
    public String showDownloadForm(Model model) {
        model.addAttribute("formats", ImageFormat.values());
        return "download";
    }

    @PostMapping("/download")
    public String handleDownload(
            @RequestParam String url,
            @RequestParam String savePath,
            @RequestParam(required = false) List<String> formats,
            Model model) {

        log.debug("Received formats from form: {}", formats);

        // Проверка на корректность URL
        if (!isValidUrl(url)) {
            throw new IllegalArgumentException("Неверный URL: " + url);
        }

        List<ImageFormat> selectedFormats = getSelectedFormats(formats);

        log.info("Processing download request for formats: {}", selectedFormats);

        DownloadResult result = downloadService.downloadImages(url, savePath, selectedFormats);

        addAttributesToModel(model, result, url, savePath, formats);

        return "download";
    }

    private boolean isValidUrl(String url) {
        // Простая проверка на наличие протокола (http/https)
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

    private void addAttributesToModel(Model model, DownloadResult result, String url, String savePath, List<String> formats) {
        model.addAttribute("result", result);
        model.addAttribute("url", url);
        model.addAttribute("savePath", savePath);
        model.addAttribute("selectedFormats", formats);
        model.addAttribute("formats", ImageFormat.values());
    }
}