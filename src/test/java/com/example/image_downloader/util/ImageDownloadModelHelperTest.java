package com.example.image_downloader.util;


import com.example.image_downloader.enums.ImageFormat;
import com.example.image_downloader.model.DownloadResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageDownloadModelHelperTest {

    @Mock
    private Model model;

    @InjectMocks
    private ImageDownloadModelHelper modelHelper;

    @Test
    void addAttributesToModel_WithAllParameters_ShouldAddAllAttributes() {
        // Arrange
        String url = "https://example.com";
        String savePath = "/path/to/save";
        List<String> formats = Arrays.asList("JPG", "PNG");
        DownloadResult result = new DownloadResult(true, "Success", 2);

        // Act
        modelHelper.addAttributesToModel(model, result, url, savePath, formats);

        // Assert
        verify(model).addAttribute("result", result);
        verify(model).addAttribute("url", url);
        verify(model).addAttribute("savePath", savePath);
        verify(model).addAttribute("selectedFormats", formats);
        verify(model).addAttribute("formats", ImageFormat.values());
        verifyNoMoreInteractions(model);
    }

    @Test
    void addAttributesToModel_WithNullFormats_ShouldAddNullSelectedFormats() {
        // Arrange
        String url = "https://example.com";
        String savePath = "/path/to/save";
        DownloadResult result = new DownloadResult(true, "Success", 1);

        // Act
        modelHelper.addAttributesToModel(model, result, url, savePath, null);

        // Assert
        verify(model).addAttribute("result", result);
        verify(model).addAttribute("url", url);
        verify(model).addAttribute("savePath", savePath);
        verify(model).addAttribute("selectedFormats", null);
        verify(model).addAttribute("formats", ImageFormat.values());
        verifyNoMoreInteractions(model);
    }

    @Test
    void addAttributesToModel_WithEmptyFormats_ShouldAddEmptySelectedFormats() {
        // Arrange
        String url = "https://example.com";
        String savePath = "/path/to/save";
        List<String> emptyFormats = Collections.emptyList();
        DownloadResult result = new DownloadResult(true, "Success", 0);

        // Act
        modelHelper.addAttributesToModel(model, result, url, savePath, emptyFormats);

        // Assert
        verify(model).addAttribute("result", result);
        verify(model).addAttribute("url", url);
        verify(model).addAttribute("savePath", savePath);
        verify(model).addAttribute("selectedFormats", emptyFormats);
        verify(model).addAttribute("formats", ImageFormat.values());
        verifyNoMoreInteractions(model);
    }

    @Test
    void addAttributesToModel_WithFailedResult_ShouldAddAllAttributes() {
        // Arrange
        String url = "https://example.com";
        String savePath = "/path/to/save";
        List<String> formats = Collections.singletonList("JPG");
        DownloadResult result = new DownloadResult(false, "Error occurred", 0);

        // Act
        modelHelper.addAttributesToModel(model, result, url, savePath, formats);

        // Assert
        verify(model).addAttribute("result", result);
        verify(model).addAttribute("url", url);
        verify(model).addAttribute("savePath", savePath);
        verify(model).addAttribute("selectedFormats", formats);
        verify(model).addAttribute("formats", ImageFormat.values());
        verifyNoMoreInteractions(model);
    }

    @Test
    void addFormatsToModel_ShouldAddOnlyFormatsAttribute() {
        // Act
        modelHelper.addFormatsToModel(model);

        // Assert
        verify(model).addAttribute("formats", ImageFormat.values());
        verifyNoMoreInteractions(model);
    }

    @Test
    void addAttributesToModel_WithNullResult_ShouldStillAddOtherAttributes() {
        // Arrange
        String url = "https://example.com";
        String savePath = "/path/to/save";
        List<String> formats = Collections.singletonList("PNG");

        // Act
        modelHelper.addAttributesToModel(model, null, url, savePath, formats);

        // Assert
        verify(model).addAttribute("result", null);
        verify(model).addAttribute("url", url);
        verify(model).addAttribute("savePath", savePath);
        verify(model).addAttribute("selectedFormats", formats);
        verify(model).addAttribute("formats", ImageFormat.values());
        verifyNoMoreInteractions(model);
    }

    @Test
    void addAttributesToModel_WithAllNullParameters_ShouldAddNullAttributes() {
        // Act
        modelHelper.addAttributesToModel(model, null, null, null, null);

        // Assert
        verify(model).addAttribute("result", null);
        verify(model).addAttribute("url", null);
        verify(model).addAttribute("savePath", null);
        verify(model).addAttribute("selectedFormats", null);
        verify(model).addAttribute("formats", ImageFormat.values());
        verifyNoMoreInteractions(model);
    }
}