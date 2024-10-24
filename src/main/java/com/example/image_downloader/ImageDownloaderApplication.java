package com.example.image_downloader;

import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.service.ImageDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageDownloaderApplication implements CommandLineRunner {

	@Autowired
	private ImageDownloadService imageDownloaderService;

	public static void main(String[] args) {
		SpringApplication.run(ImageDownloaderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String url = "https://example.com"; // Замените на реальный URL
		String savePath = "C:\\Downloads\\Images"; // Замените на реальный путь

		System.out.println("Начинаем скачивание изображений...");
		DownloadResult result = imageDownloaderService.downloadImages(url, savePath);

		System.out.println(result.getMessage());
		System.out.println("Скачано изображений: " + result.getDownloadedCount());
		if (!result.getErrors().isEmpty()) {
			System.out.println("Ошибки:");
			result.getErrors().forEach(System.out::println);
		}

		System.out.println("Процесс " + (result.isSuccess() ? "успешно завершен." : "завершен с ошибками."));
	}
}