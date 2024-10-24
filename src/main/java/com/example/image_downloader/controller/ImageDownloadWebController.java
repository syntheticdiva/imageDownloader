package com.example.image_downloader.controller;

import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.service.ImageDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "download";
    }

    @PostMapping("/download")
    public String handleDownload(
            @RequestParam String url,
            @RequestParam String savePath,
            Model model) {

        DownloadResult result = downloadService.downloadImages(url, savePath);

        model.addAttribute("result", result);
        model.addAttribute("url", url);
        model.addAttribute("savePath", savePath);

        return "download";
    }
}