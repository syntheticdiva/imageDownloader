package com.example.image_downloader.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.example.image_downloader.model.DownloadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.image_downloader.model.DownloadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private Model model;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleIOException_ShouldReturnErrorViewWithCorrectMessage() {
        // Arrange
        IOException exception = new IOException("Test IO error");

        // Act
        String viewName = (String) exceptionHandler.handleIOException(exception, model);

        // Assert
        verify(model).addAttribute(eq("result"), argThat(arg -> {
            DownloadResult result = (DownloadResult) arg;
            return !result.isSuccess() &&
                    result.getMessage().contains("Ошибка ввода/вывода") &&
                    result.getErrors().get(0).equals("Test IO error");
        }));
        assertEquals("error", viewName);
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnErrorViewWithFormattedMessage() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid URL 'http://invalid-url'");

        // Act
        String viewName = exceptionHandler.handleIllegalArgumentException(exception, model);

        // Assert
        verify(model).addAttribute(eq("result"), argThat(arg -> {
            DownloadResult result = (DownloadResult) arg;
            return !result.isSuccess() &&
                    result.getMessage().contains("Произошла ошибка при обработке вашего запроса") &&
                    result.getErrors().get(0).equals(exception.getMessage());
        }));
        assertEquals("error", viewName);
    }

    @Test
    void handleMissingParams_ShouldReturnErrorViewWithParameterName() {
        // Arrange
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("url", "String");

        // Act
        String viewName = (String) exceptionHandler.handleMissingParams(exception, model);

        // Assert
        verify(model).addAttribute(eq("result"), argThat(arg -> {
            DownloadResult result = (DownloadResult) arg;
            return !result.isSuccess() &&
                    result.getMessage().contains("Отсутствует обязательный параметр: url");
        }));
        assertEquals("error", viewName);
    }

    @Test
    void handleTypeMismatch_ShouldReturnErrorViewWithParameterDetails() {
        // Arrange
        MethodArgumentTypeMismatchException exception =
                mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("testParam");
        when(exception.getMessage()).thenReturn("Type mismatch");

        // Act
        String viewName = (String) exceptionHandler.handleTypeMismatch(exception, model);

        // Assert
        verify(model).addAttribute(eq("result"), argThat(arg -> {
            DownloadResult result = (DownloadResult) arg;
            return !result.isSuccess() &&
                    result.getMessage().contains("Неверный формат параметра: testParam");
        }));
        assertEquals("error", viewName);
    }

    @Test
    void handleNoHandlerFoundException_ShouldReturnErrorViewWith404Message() throws Exception {
        // Arrange
        NoHandlerFoundException exception =
                new NoHandlerFoundException("GET", "/invalid-path", null);

        // Act
        String viewName = (String) exceptionHandler.handleNoHandlerFoundException(exception, model);

        // Assert
        verify(model).addAttribute(eq("result"), argThat(arg -> {
            DownloadResult result = (DownloadResult) arg;
            return !result.isSuccess() &&
                    result.getMessage().contains("Запрашиваемая страница не найдена");
        }));
        assertEquals("error", viewName);
    }

    @Test
    void handleAllUncaughtException_ShouldReturnErrorViewWithGenericMessage() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error");

        // Act
        String viewName = (String) exceptionHandler.handleAllUncaughtException(exception, model);

        // Assert
        verify(model).addAttribute(eq("result"), argThat(arg -> {
            DownloadResult result = (DownloadResult) arg;
            return !result.isSuccess() &&
                    result.getMessage().contains("Произошла непредвиденная ошибка");
        }));
        assertEquals("error", viewName);
    }

    @Test
    void extractUrlFromErrorMessage_ShouldReturnCorrectUrl() {
        // Arrange
        IllegalArgumentException exception =
                new IllegalArgumentException("Invalid URL 'http://test.com'");

        // Act
        String viewName = exceptionHandler.handleIllegalArgumentException(exception, model);

        // Assert
        verify(model).addAttribute(eq("result"), argThat(arg -> {
            DownloadResult result = (DownloadResult) arg;
            return result.getMessage().contains("http://test.com");
        }));
        assertEquals("error", viewName);
    }

    @Test
    void extractUrlFromErrorMessage_WithInvalidFormat_ShouldReturnUnknownUrl() {
        // Arrange
        IllegalArgumentException exception =
                new IllegalArgumentException("Invalid URL without proper formatting");

        // Act
        String viewName = exceptionHandler.handleIllegalArgumentException(exception, model);

        // Assert
        verify(model).addAttribute(eq("result"), argThat(arg -> {
            DownloadResult result = (DownloadResult) arg;
            return result.getMessage().contains("неизвестный URL");
        }));
        assertEquals("error", viewName);
    }
}