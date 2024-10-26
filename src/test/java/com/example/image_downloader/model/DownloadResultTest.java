package com.example.image_downloader.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


class DownloadResultTest {

    @Test
    void testNoArgsConstructor() {
        // Act
        DownloadResult result = new DownloadResult();

        // Assert
        assertFalse(result.isSuccess());
        assertNull(result.getMessage());
        assertEquals(0, result.getDownloadedCount());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        boolean success = true;
        String message = "Test message";
        int downloadedCount = 5;
        List<String> errors = Arrays.asList("Error 1", "Error 2");

        // Act
        DownloadResult result = new DownloadResult(success, message, downloadedCount, errors);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(downloadedCount, result.getDownloadedCount());
        assertEquals(errors, result.getErrors());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    void testConstructorWithoutErrors() {
        // Arrange
        boolean success = true;
        String message = "Test message";
        int downloadedCount = 3;

        // Act
        DownloadResult result = new DownloadResult(success, message, downloadedCount);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(downloadedCount, result.getDownloadedCount());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testAddError() {
        // Arrange
        DownloadResult result = new DownloadResult();
        String error = "Test error";

        // Act
        result.addError(error);

        // Assert
        assertEquals(1, result.getErrors().size());
        assertEquals(error, result.getErrors().get(0));
    }

    @Test
    void testMultipleAddErrors() {
        // Arrange
        DownloadResult result = new DownloadResult();

        // Act
        result.addError("Error 1");
        result.addError("Error 2");
        result.addError("Error 3");

        // Assert
        assertEquals(3, result.getErrors().size());
        assertEquals("Error 1", result.getErrors().get(0));
        assertEquals("Error 2", result.getErrors().get(1));
        assertEquals("Error 3", result.getErrors().get(2));
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        DownloadResult result = new DownloadResult();

        // Act
        result.setSuccess(true);
        result.setMessage("Updated message");
        result.setDownloadedCount(10);
        List<String> newErrors = Arrays.asList("New error 1", "New error 2");
        result.setErrors(newErrors);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Updated message", result.getMessage());
        assertEquals(10, result.getDownloadedCount());
        assertEquals(newErrors, result.getErrors());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        DownloadResult result1 = new DownloadResult(true, "message", 5, Arrays.asList("Error"));
        DownloadResult result2 = new DownloadResult(true, "message", 5, Arrays.asList("Error"));
        DownloadResult result3 = new DownloadResult(false, "different", 3, Arrays.asList("Different error"));

        // Assert
        assertEquals(result1, result2);
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1, result3);
        assertNotEquals(result1.hashCode(), result3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        DownloadResult result = new DownloadResult(true, "Test message", 5, Arrays.asList("Error 1"));

        // Act
        String toString = result.toString();

        // Assert
        assertTrue(toString.contains("success=true"));
        assertTrue(toString.contains("message=Test message"));
        assertTrue(toString.contains("downloadedCount=5"));
        assertTrue(toString.contains("Error 1"));
    }

    @Test
    void testErrorsListMutability() {
        // Arrange
        List<String> initialErrors = new ArrayList<>(Arrays.asList("Error 1"));
        DownloadResult result = new DownloadResult(true, "message", 1, initialErrors);

        // Act
        initialErrors.add("Error 2");

        // Assert
        assertEquals(2, result.getErrors().size());
        assertEquals("Error 1", result.getErrors().get(0));
    }
}