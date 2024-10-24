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
