package com.example.image_downloader.enums;

public enum DownloadStatus {
    PENDING("Ожидание загрузки"),
    IN_PROGRESS("Загрузка в процессе"),
    COMPLETED("Загрузка завершена"),
    FAILED("Ошибка загрузки"),
    CANCELLED("Загрузка отменена");

    private final String description;

    DownloadStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
