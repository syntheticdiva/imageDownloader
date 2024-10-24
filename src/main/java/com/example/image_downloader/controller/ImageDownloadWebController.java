package com.example.image_downloader.controller;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.service.ImageDownloadService;
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

    private final ImageDownloadService downloadService;

    @Autowired
    public ImageDownloadWebController(ImageDownloadService downloadService) {
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

        List<ImageFormat> selectedFormats;
        if (formats != null && formats.contains("ALL")) {
            selectedFormats = Collections.singletonList(ImageFormat.ALL);
        } else if (formats != null && !formats.isEmpty()) {
            selectedFormats = formats.stream()
                    .map(ImageFormat::valueOf)
                    .collect(Collectors.toList());
        } else {
            // Если ничего не выбрано, используем все форматы по умолчанию
            selectedFormats = Collections.singletonList(ImageFormat.ALL);
        }

        log.info("Processing download request for formats: {}", selectedFormats);

        DownloadResult result = downloadService.downloadImages(url, savePath, selectedFormats);
        model.addAttribute("result", result);



        model.addAttribute("result", result);
        model.addAttribute("url", url);
        model.addAttribute("savePath", savePath);
        model.addAttribute("selectedFormats", formats);
        model.addAttribute("formats", ImageFormat.values());

        return "download";
    }
}