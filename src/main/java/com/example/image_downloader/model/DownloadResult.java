package com.example.image_downloader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResult {
    private boolean success;
    private String message;
    private int downloadedCount;
    private List<String> errors;

    public DownloadResult(boolean success, String message, int downloadedCount) {
        this.success = success;
        this.message = message;
        this.downloadedCount = downloadedCount;
        this.errors = new ArrayList<>();
    }

    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
    }
}