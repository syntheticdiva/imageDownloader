package com.example.image_downloader;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.service.ImageDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
@SpringBootApplication
public class ImageDownloaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageDownloaderApplication.class, args);
	}
}
//@SpringBootApplication
//@Slf4j // Добавьте эту аннотацию для логирования
//public class ImageDownloaderApplication implements CommandLineRunner {
//
//	@Autowired
//	private ImageDownloadService imageDownloaderService;
//
//	public static void main(String[] args) {
//		SpringApplication.run(ImageDownloaderApplication.class, args);
//	}
//
//	@Override
//	public void run(String... args) throws Exception {
//		if (args.length < 2) {
//			log.error("Необходимо указать URL и путь сохранения");
//			return;
//		}
//
//		String url = args[0];
//		String savePath = args[1];
//		ImageFormat format = ImageFormat.ALL;
//
//		if (args.length > 2) {
//			try {
//				format = ImageFormat.valueOf(args[2].toUpperCase());
//			} catch (IllegalArgumentException e) {
//				log.warn("Неверный формат. Используется формат по умолчанию: {}", format);
//			}
//		}
//
//		log.info("Начинаем скачивание изображений...");
//		log.info("URL: {}", url);
//		log.info("Путь сохранения: {}", savePath);
//		log.info("Выбранный формат: {}", format);
//
//		try {
//			DownloadResult result = imageDownloaderService.downloadImages(url, savePath, Collections.singletonList(format));
//
//			if (result != null) {
//				log.info(result.getMessage());
//				log.info("Скачано изображений: {}", result.getDownloadedCount());
//				if (!result.getErrors().isEmpty()) {
//					log.error("Ошибки:");
//					result.getErrors().forEach(log::error);
//				}
//				log.info("Процесс {}", result.isSuccess() ? "успешно завершен." : "завершен с ошибками.");
//			} else {
//				log.error("Ошибка: Результат скачивания null. Проверьте реализацию ImageDownloadService.");
//			}
//		} catch (Exception e) {
//			log.error("Произошла ошибка при скачивании изображений: {}", e.getMessage(), e);
//		}
//	}
//}