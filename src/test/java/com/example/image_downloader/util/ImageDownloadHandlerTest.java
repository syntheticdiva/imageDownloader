package com.example.image_downloader.util;

import static org.junit.jupiter.api.Assertions.*;

import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.service.impl.ImageDownloadServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageDownloadHandlerTest {

    @Mock
    private ImageDownloadServiceImpl downloadService;

    @InjectMocks
    private ImageDownloadHandler handler;

    private static final String VALID_URL = "https://example.com";
    private static final String VALID_SAVE_PATH = "/path/to/save";

    @Test
    void processDownloadRequest_WithValidParameters_ShouldReturnDownloadResult() {
        // Arrange
        List<String> formats = Arrays.asList("JPG", "PNG");
        DownloadResult expectedResult = new DownloadResult(true, "Success", 2);
        when(downloadService.downloadImages(eq(VALID_URL), eq(VALID_SAVE_PATH), any()))
                .thenReturn(expectedResult);

        // Act
        DownloadResult result = handler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats);

        // Assert
        assertEquals(expectedResult, result);
        verify(downloadService).downloadImages(eq(VALID_URL), eq(VALID_SAVE_PATH),
                argThat(list -> list.contains(ImageFormat.JPG) && list.contains(ImageFormat.PNG)));
    }

    @Test
    void processDownloadRequest_WithInvalidUrl_ShouldThrowException() {
        // Arrange
        String invalidUrl = "invalid-url";
        List<String> formats = Collections.singletonList("JPG");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                handler.processDownloadRequest(invalidUrl, VALID_SAVE_PATH, formats));
        verify(downloadService, never()).downloadImages(any(), any(), any());
    }

    @Test
    void processDownloadRequest_WithAllFormat_ShouldUseAllImageFormat() {
        // Arrange
        List<String> formats = Collections.singletonList("ALL");
        DownloadResult expectedResult = new DownloadResult(true, "Success", 1);
        when(downloadService.downloadImages(any(), any(), any())).thenReturn(expectedResult);

        // Act
        DownloadResult result = handler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats);

        // Assert
        assertEquals(expectedResult, result);
        verify(downloadService).downloadImages(eq(VALID_URL), eq(VALID_SAVE_PATH),
                eq(Collections.singletonList(ImageFormat.ALL)));
    }

    @Test
    void processDownloadRequest_WithNullFormats_ShouldUseAllImageFormat() {
        // Arrange
        DownloadResult expectedResult = new DownloadResult(true, "Success", 1);
        when(downloadService.downloadImages(any(), any(), any())).thenReturn(expectedResult);

        // Act
        DownloadResult result = handler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, null);

        // Assert
        assertEquals(expectedResult, result);
        verify(downloadService).downloadImages(eq(VALID_URL), eq(VALID_SAVE_PATH),
                eq(Collections.singletonList(ImageFormat.ALL)));
    }

    @Test
    void processDownloadRequest_WithEmptyFormats_ShouldUseAllImageFormat() {
        // Arrange
        List<String> emptyFormats = Collections.emptyList();
        DownloadResult expectedResult = new DownloadResult(true, "Success", 1);
        when(downloadService.downloadImages(any(), any(), any())).thenReturn(expectedResult);

        // Act
        DownloadResult result = handler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, emptyFormats);

        // Assert
        assertEquals(expectedResult, result);
        verify(downloadService).downloadImages(eq(VALID_URL), eq(VALID_SAVE_PATH),
                eq(Collections.singletonList(ImageFormat.ALL)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://example.com", "https://example.com"})
    void validateUrl_WithValidUrls_ShouldNotThrowException(String validUrl) {
        // Act & Assert
        assertDoesNotThrow(() -> handler.validateUrl(validUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ftp://example.com", "example.com", "invalid-url"})
    void validateUrl_WithInvalidUrls_ShouldThrowException(String invalidUrl) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.validateUrl(invalidUrl));
    }

    @Test
    void processDownloadRequest_WithMixedFormats_ShouldProcessCorrectly() {
        // Arrange
        List<String> formats = Arrays.asList("JPG", "PNG", "ALL");
        DownloadResult expectedResult = new DownloadResult(true, "Success", 3);
        when(downloadService.downloadImages(any(), any(), any())).thenReturn(expectedResult);

        // Act
        DownloadResult result = handler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats);

        // Assert
        assertEquals(expectedResult, result);
        verify(downloadService).downloadImages(eq(VALID_URL), eq(VALID_SAVE_PATH),
                eq(Collections.singletonList(ImageFormat.ALL)));
    }

    @Test
    void processDownloadRequest_WhenServiceThrowsException_ShouldPropagateException() {
        // Arrange
        when(downloadService.downloadImages(any(), any(), any()))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                handler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, Collections.singletonList("JPG")));
    }

    @Test
    void processDownloadRequest_WithInvalidImageFormat_ShouldThrowException() {
        // Arrange
        List<String> invalidFormats = Collections.singletonList("INVALID_FORMAT");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                handler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, invalidFormats));
    }
}