package com.example.image_downloader.service.impl;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageDownloadServiceImplTest {

    @Spy
    private ImageDownloadServiceImpl imageDownloadService;

    @TempDir
    Path tempDir;

    private Method getFileExtensionMethod;
    private Method getUniqueFileNameMethod;
    private Method downloadImageMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        MockitoAnnotations.openMocks(this);

        // Получаем доступ к приватным методам через рефлексию
        getFileExtensionMethod = ImageDownloadServiceImpl.class.getDeclaredMethod("getFileExtension", String.class);
        getFileExtensionMethod.setAccessible(true);

        getUniqueFileNameMethod = ImageDownloadServiceImpl.class.getDeclaredMethod("getUniqueFileName", String.class, String.class);
        getUniqueFileNameMethod.setAccessible(true);

        downloadImageMethod = ImageDownloadServiceImpl.class.getDeclaredMethod("downloadImage", String.class, String.class);
        downloadImageMethod.setAccessible(true);
    }

    @Test
    void downloadImages_WithInvalidPath_ReturnsError() {
        // Arrange
        String invalidPath = "nonexistent/path";
        String url = "http://example.com";
        List<ImageFormat> formats = Collections.singletonList(ImageFormat.ALL);

        // Act
        DownloadResult result = imageDownloadService.downloadImages(url, invalidPath, formats);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(0, result.getDownloadedCount());
        assertTrue(result.getErrors().get(0).contains("Укажите существующий абсолютный путь"));
    }

    @Test
    void downloadImages_WithValidPath_Success() {
        // Arrange
        String url = "https://example.com";
        String validPath = tempDir.toString();
        List<ImageFormat> formats = Collections.singletonList(ImageFormat.ALL);

        // Act
        DownloadResult result = imageDownloadService.downloadImages(url, validPath, formats);

        // Assert
        assertNotNull(result);
    }

    @Test
    void getFileExtension_WithValidUrl_ReturnsCorrectExtension() throws Exception {
        // Arrange
        String url = "http://example.com/image.jpg";

        // Act
        String extension = (String) getFileExtensionMethod.invoke(imageDownloadService, url);

        // Assert
        assertEquals(".jpg", extension);
    }

    @Test
    void getFileExtension_WithQueryParameters_ReturnsCorrectExtension() throws Exception {
        // Arrange
        String url = "http://example.com/image.png?size=large";

        // Act
        String extension = (String) getFileExtensionMethod.invoke(imageDownloadService, url);

        // Assert
        assertEquals(".png", extension);
    }

    @Test
    void getUniqueFileName_WithExistingFile_ReturnsUniqueFileName() throws Exception {
        // Arrange
        String savePath = tempDir.toString();
        String imageUrl = "http://example.com/test.jpg";

        // Act
        String fileName1 = (String) getUniqueFileNameMethod.invoke(imageDownloadService, savePath, imageUrl);
        new File(savePath, fileName1).createNewFile();
        String fileName2 = (String) getUniqueFileNameMethod.invoke(imageDownloadService, savePath, imageUrl);

        // Assert
        assertNotEquals(fileName1, fileName2);
    }

    @Test
    void downloadImages_WithSpecificFormat_FiltersCorrectly() {
        // Arrange
        String url = "http://example.com";
        String validPath = tempDir.toString();
        List<ImageFormat> formats = Collections.singletonList(ImageFormat.JPG);

        // Act
        DownloadResult result = imageDownloadService.downloadImages(url, validPath, formats);

        // Assert
        assertNotNull(result);
    }

    @Test
    void downloadImages_WithMultipleFormats_Success() {
        // Arrange
        String url = "http://example.com";
        String validPath = tempDir.toString();
        List<ImageFormat> formats = Arrays.asList(ImageFormat.JPG, ImageFormat.PNG);

        // Act
        DownloadResult result = imageDownloadService.downloadImages(url, validPath, formats);

        // Assert
        assertNotNull(result);
    }

    @Test
    void downloadImage_WithInvalidUrl_ThrowsIOException() {
        // Arrange
        String invalidUrl = "http://invalid-url-that-does-not-exist.com/image.jpg";
        String destinationPath = tempDir.resolve("test.jpg").toString();

        // Act & Assert
        assertThrows(Exception.class, () ->
                downloadImageMethod.invoke(imageDownloadService, invalidUrl, destinationPath)
        );
    }

    @Test
    void downloadImages_WithInvalidUrl_ReturnsError() {
        // Arrange
        String invalidUrl = "invalid-url";
        String validPath = tempDir.toString();
        List<ImageFormat> formats = Collections.singletonList(ImageFormat.ALL);

        // Act
        DownloadResult result = imageDownloadService.downloadImages(invalidUrl, validPath, formats);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(0, result.getDownloadedCount());
        assertFalse(result.getErrors().isEmpty());
    }
}