package com.example.image_downloader.controller;

import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.util.ImageDownloadHandler;
import com.example.image_downloader.util.ImageDownloadModelHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
public class ImageDownloadWebController {

    private final ImageDownloadHandler downloadHandler;
    private final ImageDownloadModelHelper modelHelper;

    @Autowired
    public ImageDownloadWebController(ImageDownloadHandler downloadHandler,
                                      ImageDownloadModelHelper modelHelper) {
        this.downloadHandler = downloadHandler;
        this.modelHelper = modelHelper;
    }

    @GetMapping("/")
    public String showDownloadForm(Model model) {
        modelHelper.addFormatsToModel(model);
        return "download";
    }

    @PostMapping("/download")
    public String handleDownload(
            @RequestParam String url,
            @RequestParam String savePath,
            @RequestParam(required = false) List<String> formats,
            Model model) {

        DownloadResult result = downloadHandler.processDownloadRequest(url, savePath, formats);
        modelHelper.addAttributesToModel(model, result, url, savePath, formats);
        return "download";
    }
}