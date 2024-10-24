package com.example.image_downloader.model;

import com.example.image_downloader.enums.DownloadStatus;
import lombok.Data;

@Data
public class DownloadTask {
    private String id;
    private String url;
    private String savePath;
    private DownloadStatus status;
    private DownloadResult result;

    public DownloadTask(String id, String url, String savePath) {
        this.id = id;
        this.url = url;
        this.savePath = savePath;
        this.status = DownloadStatus.PENDING;
    }
}