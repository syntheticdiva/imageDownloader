package com.example.image_downloader.exception;

import com.example.image_downloader.model.DownloadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        log.error("Unexpected error occurred", e);

        DownloadResult errorResult = new DownloadResult(
                false,
                "Произошла непредвиденная ошибка",
                0,
                Collections.singletonList("Пожалуйста, проверьте введенные данные и попробуйте снова")
        );

        model.addAttribute("result", errorResult);
        return "download";
    }

    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException e, Model model) {
        log.error("IO error occurred", e);

        DownloadResult errorResult = new DownloadResult(
                false,
                "Ошибка ввода/вывода",
                0,
                Arrays.asList(
                        "Проверьте подключение к интернету",
                        "Убедитесь, что указанный путь существует и доступен для записи",
                        "Проверьте корректность URL"
                )
        );

        model.addAttribute("result", errorResult);
        return "download";
    }
}