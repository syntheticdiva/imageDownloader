package com.example.image_downloader.service;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;

import java.util.List;

public interface ImageDownloadService {
    DownloadResult downloadImages(String url, String savePath, List<ImageFormat> formats);
}
