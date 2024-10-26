package com.example.image_downloader.util;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

@Component
public class ImageDownloadModelHelper {
    public void addAttributesToModel(Model model, DownloadResult result, String url,
                                     String savePath, List<String> formats) {
        model.addAttribute("result", result);
        model.addAttribute("url", url);
        model.addAttribute("savePath", savePath);
        model.addAttribute("selectedFormats", formats);
        model.addAttribute("formats", ImageFormat.values());
    }

    public void addFormatsToModel(Model model) {
        model.addAttribute("formats", ImageFormat.values());
    }
}
