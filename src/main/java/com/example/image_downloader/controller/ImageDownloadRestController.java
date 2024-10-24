package com.example.image_downloader.controller;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.model.DownloadTask;
import com.example.image_downloader.service.ImageDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api")
@Slf4j
public class ImageDownloadRestController {

    private final ImageDownloadService downloadService;

    @Autowired
    public ImageDownloadRestController(ImageDownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @PostMapping("/download")
    public ResponseEntity<DownloadResult> downloadImages(
            @RequestParam String url,
            @RequestParam String savePath,
            @RequestParam(defaultValue = "ALL") ImageFormat format) {

        DownloadResult result = downloadService.downloadImages(url, savePath, Collections.singletonList(format));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/download/task")
    public ResponseEntity<DownloadTask> createDownloadTask(
            @RequestParam String url,
            @RequestParam String savePath,
            @RequestParam(defaultValue = "ALL") ImageFormat format) {
        DownloadTask task = downloadService.createDownloadTask(url, savePath);
        task.setFormat(format);  // Предполагается, что вы добавите поле format в DownloadTask
        return ResponseEntity.ok(task);
    }


    @GetMapping("/download/task/{taskId}")
    public ResponseEntity<DownloadTask> getDownloadTask(@PathVariable String taskId) {
        DownloadTask task = downloadService.getDownloadTask(taskId);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/download/task/{taskId}/start")
    public ResponseEntity<DownloadTask> startDownload(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "ALL") ImageFormat format) {
        downloadService.startDownload(taskId, Collections.singletonList(format));
        DownloadTask task = downloadService.getDownloadTask(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/download/task/{taskId}/cancel")
    public ResponseEntity<DownloadTask> cancelDownload(@PathVariable String taskId) {
        downloadService.cancelDownload(taskId);
        DownloadTask task = downloadService.getDownloadTask(taskId);
        return ResponseEntity.ok(task);
    }
}