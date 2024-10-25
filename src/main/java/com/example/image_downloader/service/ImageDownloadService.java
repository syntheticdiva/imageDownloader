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
        log.info("Начало скачивания изображений с URL: {} в путь: {} с форматами: {}", url, savePath, formats);
        List<String> errors = new ArrayList<>();
        int downloadedCount = 0;

        // Проверка пути сохранения
        File saveDirectory = new File(savePath);
        if (!saveDirectory.isAbsolute() || !saveDirectory.exists() || !saveDirectory.isDirectory()) {
            String errorMessage = "Указан некорректный путь для сохранения: " + savePath;
            log.error(errorMessage);
            return new DownloadResult(false, errorMessage, 0,
                    Collections.singletonList("Укажите существующий абсолютный путь к директории"));
        }

        log.info("Путь для сохранения подтвержден: {}", savePath);

        try {
            Document doc = Jsoup.connect(url).get();
            Elements images = doc.select("img[src]");
            log.info("Найдено {} изображений на странице", images.size());

            for (Element img : images) {
                String imgUrl = img.attr("abs:src");
                if (imgUrl.isEmpty()) {
                    log.debug("Пропуск пустого URL изображения");
                    continue;
                }

                String fileExtension = getFileExtension(imgUrl);
                log.debug("Обработка изображения: {} с расширением: {}", imgUrl, fileExtension);

                boolean isValidFormat = formats.contains(ImageFormat.ALL) ||
                        formats.stream().anyMatch(format ->
                                ImageFormat.isValidExtension(fileExtension, format));

                if (!isValidFormat) {
                    log.debug("Пропуск изображения из-за несоответствия формата: {}", imgUrl);
                    continue;
                }

                try {
                    String fileName = getUniqueFileName(savePath, imgUrl);
                    String fullPath = savePath + File.separator + fileName;

                    downloadImage(imgUrl, fullPath);
                    downloadedCount++;
                    log.info("Успешно скачано: {} в {}", imgUrl, fullPath);
                } catch (IOException e) {
                    log.error("Не удалось скачать или сохранить изображение: {}", imgUrl, e);
                    errors.add("Не удалось скачать или сохранить изображение: " + imgUrl + " - " + e.getMessage());
                }
            }

            String message = downloadedCount > 0
                    ? "Успешно скачано " + downloadedCount + " изображений в " + savePath
                    : "Не удалось скачать ни одного изображения. Возможно, не найден выбранный формат или проблема с доступом к директории.";
            log.info(message);

            return new DownloadResult(errors.isEmpty(), message, downloadedCount, errors);

        } catch (IOException e) {
            log.error("Не удалось загрузить страницу: {}", url, e);
            return new DownloadResult(false, "Не удалось загрузить страницу", 0,
                    Collections.singletonList("Проверьте подключение к интернету и корректность URL"));
        } catch (Exception e) {
            log.error("Непредвиденная ошибка во время процесса скачивания", e);
            return new DownloadResult(false, "Произошла непредвиденная ошибка", downloadedCount,
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

        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

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
                log.error("Ошибка при выполнении задачи скачивания", e);
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
        log.info("Изображение успешно скачано и сохранено в: {}", destinationPath);
    }
}
