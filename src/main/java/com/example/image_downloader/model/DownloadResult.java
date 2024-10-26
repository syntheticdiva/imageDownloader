package com.example.image_downloader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadResult {
    private boolean success;
    private String message;
    private int downloadedCount;
    private List<String> errors = new ArrayList<>();

    public DownloadResult(boolean success, String message, int downloadedCount) {
        this.success = success;
        this.message = message;
        this.downloadedCount = downloadedCount;
    }

    public void addError(String error) {
        this.errors.add(error);
    }
}