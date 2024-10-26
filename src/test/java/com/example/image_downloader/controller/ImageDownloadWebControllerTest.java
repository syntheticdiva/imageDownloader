package com.example.image_downloader.controller;

import com.example.image_downloader.model.DownloadResult;
import com.example.image_downloader.util.ImageDownloadHandler;
import com.example.image_downloader.util.ImageDownloadModelHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageDownloadWebControllerTest {

    @Mock
    private ImageDownloadHandler downloadHandler;

    @Mock
    private ImageDownloadModelHelper modelHelper;

    @Mock
    private Model model;

    @InjectMocks
    private ImageDownloadWebController controller;

    private static final String VALID_URL = "https://example.com";
    private static final String VALID_SAVE_PATH = "/path/to/save";

    @Test
    void showDownloadForm_ShouldAddFormatsAndReturnDownloadView() {
        // Act
        String viewName = controller.showDownloadForm(model);

        // Assert
        verify(modelHelper).addFormatsToModel(model);
        assertEquals("download", viewName);
    }

    @Test
    void handleDownload_WithValidParameters_ShouldProcessRequestAndReturnDownloadView() {
        // Arrange
        List<String> formats = Arrays.asList("JPG", "PNG");
        DownloadResult mockResult = new DownloadResult(true, "Success", 2);

        when(downloadHandler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats))
                .thenReturn(mockResult);

        // Act
        String viewName = controller.handleDownload(VALID_URL, VALID_SAVE_PATH, formats, model);

        // Assert
        verify(downloadHandler).processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats);
        verify(modelHelper).addAttributesToModel(model, mockResult, VALID_URL, VALID_SAVE_PATH, formats);
        assertEquals("download", viewName);
    }

    @Test
    void handleDownload_WithNullFormats_ShouldProcessRequestWithNullFormats() {
        // Arrange
        DownloadResult mockResult = new DownloadResult(true, "Success", 1);

        when(downloadHandler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, null))
                .thenReturn(mockResult);

        // Act
        String viewName = controller.handleDownload(VALID_URL, VALID_SAVE_PATH, null, model);

        // Assert
        verify(downloadHandler).processDownloadRequest(VALID_URL, VALID_SAVE_PATH, null);
        verify(modelHelper).addAttributesToModel(model, mockResult, VALID_URL, VALID_SAVE_PATH, null);
        assertEquals("download", viewName);
    }

    @Test
    void handleDownload_WithEmptyFormats_ShouldProcessRequestWithEmptyFormats() {
        // Arrange
        List<String> emptyFormats = Collections.emptyList();
        DownloadResult mockResult = new DownloadResult(true, "Success", 0);

        when(downloadHandler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, emptyFormats))
                .thenReturn(mockResult);

        // Act
        String viewName = controller.handleDownload(VALID_URL, VALID_SAVE_PATH, emptyFormats, model);

        // Assert
        verify(downloadHandler).processDownloadRequest(VALID_URL, VALID_SAVE_PATH, emptyFormats);
        verify(modelHelper).addAttributesToModel(model, mockResult, VALID_URL, VALID_SAVE_PATH, emptyFormats);
        assertEquals("download", viewName);
    }

    @Test
    void handleDownload_WhenHandlerThrowsException_ShouldStillAddAttributesToModel() {
        // Arrange
        List<String> formats = Collections.singletonList("JPG");
        DownloadResult errorResult = new DownloadResult(false, "Error", 0);

        when(downloadHandler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats))
                .thenReturn(errorResult);

        // Act
        String viewName = controller.handleDownload(VALID_URL, VALID_SAVE_PATH, formats, model);

        // Assert
        verify(downloadHandler).processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats);
        verify(modelHelper).addAttributesToModel(model, errorResult, VALID_URL, VALID_SAVE_PATH, formats);
        assertEquals("download", viewName);
    }

    @Test
    void handleDownload_WithAllPossibleFormats_ShouldProcessRequest() {
        // Arrange
        List<String> formats = Arrays.asList("JPG", "PNG", "GIF", "BMP");
        DownloadResult mockResult = new DownloadResult(true, "Success", 4);

        when(downloadHandler.processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats))
                .thenReturn(mockResult);

        // Act
        String viewName = controller.handleDownload(VALID_URL, VALID_SAVE_PATH, formats, model);

        // Assert
        verify(downloadHandler).processDownloadRequest(VALID_URL, VALID_SAVE_PATH, formats);
        verify(modelHelper).addAttributesToModel(model, mockResult, VALID_URL, VALID_SAVE_PATH, formats);
        assertEquals("download", viewName);
    }
}