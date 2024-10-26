package com.example.image_downloader.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


class ImageFormatTest {

    @Test
    void getExtensions_ALL_ShouldReturnAllExtensions() {
        // Act
        List<String> extensions = ImageFormat.getExtensions(ImageFormat.ALL);

        // Assert
        assertEquals(5, extensions.size());
        assertTrue(extensions.containsAll(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp")));
    }

    @Test
    void getExtensions_JPG_ShouldReturnJpgExtensions() {
        // Act
        List<String> extensions = ImageFormat.getExtensions(ImageFormat.JPG);

        // Assert
        assertEquals(2, extensions.size());
        assertTrue(extensions.containsAll(Arrays.asList(".jpg", ".jpeg")));
    }

    @Test
    void getExtensions_JPEG_ShouldReturnJpegExtensions() {
        // Act
        List<String> extensions = ImageFormat.getExtensions(ImageFormat.JPEG);

        // Assert
        assertEquals(2, extensions.size());
        assertTrue(extensions.containsAll(Arrays.asList(".jpg", ".jpeg")));
    }

    @Test
    void getExtensions_PNG_ShouldReturnPngExtension() {
        // Act
        List<String> extensions = ImageFormat.getExtensions(ImageFormat.PNG);

        // Assert
        assertEquals(1, extensions.size());
        assertEquals(".png", extensions.get(0));
    }

    @Test
    void getExtensions_GIF_ShouldReturnGifExtension() {
        // Act
        List<String> extensions = ImageFormat.getExtensions(ImageFormat.GIF);

        // Assert
        assertEquals(1, extensions.size());
        assertEquals(".gif", extensions.get(0));
    }

    @Test
    void getExtensions_BMP_ShouldReturnBmpExtension() {
        // Act
        List<String> extensions = ImageFormat.getExtensions(ImageFormat.BMP);

        // Assert
        assertEquals(1, extensions.size());
        assertEquals(".bmp", extensions.get(0));
    }

    @ParameterizedTest
    @EnumSource(ImageFormat.class)
    void getExtensions_ShouldNotReturnNull(ImageFormat format) {
        // Act
        List<String> extensions = ImageFormat.getExtensions(format);

        // Assert
        assertNotNull(extensions);
        assertFalse(extensions.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideValidExtensionsAndFormats")
    void isValidExtension_WithValidExtension_ShouldReturnTrue(String extension, ImageFormat format) {
        // Act & Assert
        assertTrue(ImageFormat.isValidExtension(extension, format));
    }

    private static Stream<Arguments> provideValidExtensionsAndFormats() {
        return Stream.of(
                Arguments.of(".jpg", ImageFormat.JPG),
                Arguments.of(".jpeg", ImageFormat.JPEG),
                Arguments.of(".png", ImageFormat.PNG),
                Arguments.of(".gif", ImageFormat.GIF),
                Arguments.of(".bmp", ImageFormat.BMP),
                Arguments.of(".jpg", ImageFormat.ALL),
                Arguments.of(".png", ImageFormat.ALL),
                Arguments.of(".gif", ImageFormat.ALL),
                Arguments.of(".bmp", ImageFormat.ALL)
        );
    }

    @Test
    void isValidExtension_WithMixedCase_ShouldBeCaseInsensitive() {
        // Act & Assert
        assertTrue(ImageFormat.isValidExtension(".JPG", ImageFormat.JPG));
        assertTrue(ImageFormat.isValidExtension(".Png", ImageFormat.PNG));
        assertTrue(ImageFormat.isValidExtension(".GiF", ImageFormat.GIF));
    }

    @Test
    void isValidExtension_WithALLFormat_ShouldAcceptAllValidExtensions() {
        // Arrange
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};

        // Act & Assert
        for (String extension : validExtensions) {
            assertTrue(ImageFormat.isValidExtension(extension, ImageFormat.ALL));
        }
    }

    @Test
    void enumValues_ShouldContainAllExpectedValues() {
        // Act
        ImageFormat[] values = ImageFormat.values();

        // Assert
        assertEquals(6, values.length);
        assertTrue(Arrays.asList(values).containsAll(
                Arrays.asList(ImageFormat.JPG, ImageFormat.JPEG, ImageFormat.PNG,
                        ImageFormat.GIF, ImageFormat.BMP, ImageFormat.ALL)
        ));
    }

    @Test
    void valueOf_ShouldReturnCorrectEnum() {
        // Act & Assert
        assertEquals(ImageFormat.JPG, ImageFormat.valueOf("JPG"));
        assertEquals(ImageFormat.PNG, ImageFormat.valueOf("PNG"));
        assertEquals(ImageFormat.ALL, ImageFormat.valueOf("ALL"));
    }

    @Test
    void valueOf_WithInvalidValue_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ImageFormat.valueOf("INVALID"));
    }
}