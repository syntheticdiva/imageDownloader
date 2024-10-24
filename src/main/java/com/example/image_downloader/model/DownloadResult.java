package com.example.image_downloader.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DownloadResult {
    private boolean success;
    private String message;
    private int downloadedCount;
    private List<String> errors;
}