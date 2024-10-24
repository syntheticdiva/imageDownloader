package com.example.image_downloader.service;

import com.example.image_downloader.enums.DownloadStatus;
import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.model.DownloadTask;
import lombok.extern.slf4j.Slf4j;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Service
@Slf4j
public class ImageDownloadService {
    private final Map<String, DownloadTask> tasks = new HashMap<>();

    public DownloadResult downloadImages(String url, String savePath, List<ImageFormat> formats) {
        log.info("Downloading images with formats: {}", formats);
        List<String> errors = new ArrayList<>();
        int downloadedCount = 0;

        try {
            // Проверяем директорию
            File directory = new File(savePath);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return new DownloadResult(false,
                            "Не удалось создать директорию для сохранения", 0,
                            Collections.singletonList("Проверьте права доступа к указанной директории"));
                }
            }

            if (!directory.canWrite()) {
                return new DownloadResult(false,
                        "Нет прав на запись в указанную директорию", 0,
                        Collections.singletonList("Выберите другую директорию или измените права доступа"));
            }

            // Загружаем и парсим страницу
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                return new DownloadResult(false,
                        "Не удалось загрузить страницу",
                        0,
                        Collections.singletonList("Проверьте подключение к интернету и корректность URL"));
            }

            // Получаем все изображения
            Elements images = doc.select("img[src]");

            for (Element img : images) {
                String imgUrl = img.attr("abs:src");
                if (imgUrl.isEmpty()) continue;

                String fileExtension = getFileExtension(imgUrl);
                log.debug("Processing image: {} with extension: {}", imgUrl, fileExtension);

                boolean isValidFormat = formats.contains(ImageFormat.ALL) ||
                        formats.stream().anyMatch(format ->
                                ImageFormat.isValidExtension(fileExtension, format));

                if (!isValidFormat) {
                    log.debug("Skipping image due to format mismatch: {}", imgUrl);
                    continue;
                }

                try {
                    String fileName = getUniqueFileName(savePath, imgUrl);
                    downloadImage(imgUrl, savePath + File.separator + fileName);
                    downloadedCount++;
                    log.debug("Successfully downloaded: {}", fileName);
                } catch (IOException e) {
                    log.error("Failed to download image: {}", imgUrl, e);
                    errors.add("Не удалось скачать изображение: " + imgUrl + " - " + e.getMessage());
                }
            }

            // Формирование результата
            String message = downloadedCount > 0
                    ? "Успешно скачано " + downloadedCount + " изображений."
                    : "Не удалось скачать ни одного изображения. Не найден выбранный формат";

            return new DownloadResult(errors.isEmpty(), message, downloadedCount, errors);

        } catch (Exception e) {
            log.error("Unexpected error", e);
            return new DownloadResult(false,
                    "Произошла непредвиденная ошибка",
                    downloadedCount,
                    Collections.singletonList("Внутренняя ошибка: " + e.getMessage()));
        }
    }
    private String getFileExtension(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        int queryStart = fileName.indexOf('?');
        if (queryStart > 0) {
            fileName = fileName.substring(0, queryStart);
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(dotIndex).toLowerCase() : "";
    }
    private String getUniqueFileName(String savePath, String imageUrl) {
        String originalFileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        if (originalFileName.contains("?")) {
            originalFileName = originalFileName.substring(0, originalFileName.indexOf("?"));
        }

        if (originalFileName.isEmpty() || !originalFileName.contains(".")) {
            originalFileName = "image.jpg";
        }

        String baseName;
        String extension;
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
            baseName = originalFileName.substring(0, lastDotIndex);
            extension = originalFileName.substring(lastDotIndex);
        } else {
            baseName = "image";
            extension = ".jpg";
        }

        File file = new File(savePath, originalFileName);
        int counter = 1;

        while (file.exists()) {
            String newFileName = baseName + "_" + counter + extension;
            file = new File(savePath, newFileName);
            counter++;
        }

        return file.getName();
    }

    public DownloadTask createDownloadTask(String url, String savePath) {
        String taskId = UUID.randomUUID().toString();
        DownloadTask task = new DownloadTask(taskId, url, savePath);
        tasks.put(taskId, task);
        return task;
    }

    public DownloadTask getDownloadTask(String taskId) {
        return tasks.get(taskId);
    }
    public void startDownload(String taskId, List<ImageFormat> formats) {
        DownloadTask task = tasks.get(taskId);
        if (task != null && task.getStatus() == DownloadStatus.PENDING) {
            task.setStatus(DownloadStatus.IN_PROGRESS);
            try {
                DownloadResult result = downloadImages(task.getUrl(), task.getSavePath(), formats);
                task.setResult(result);
                task.setStatus(result.isSuccess() ? DownloadStatus.COMPLETED : DownloadStatus.FAILED);
            } catch (Exception e) {
                log.error("Error during download task execution", e);
                task.setStatus(DownloadStatus.FAILED);
                task.setResult(new DownloadResult(false, "Ошибка при выполнении задачи: " + e.getMessage(), 0, Collections.singletonList(e.getMessage())));
            }
        }
    }

    public void cancelDownload(String taskId) {
        DownloadTask task = tasks.get(taskId);
        if (task != null && task.getStatus() == DownloadStatus.PENDING) {
            task.setStatus(DownloadStatus.CANCELLED);
        }
    }

    private void downloadImage(String imageUrl, String destinationPath) throws IOException {
        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(destinationPath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }


}