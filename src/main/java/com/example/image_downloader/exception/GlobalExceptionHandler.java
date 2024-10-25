package com.example.image_downloader.exception;

import com.example.image_downloader.model.DownloadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleIOException(IOException e, Model model) {
        log.error("IO error occurred", e);
        return createErrorResponse("Ошибка ввода/вывода при обработке запроса", e.getMessage(), model);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        log.error("Invalid argument provided", e);

        String originalMessage = e.getMessage();
        String userFriendlyMessage = "Произошла ошибка при обработке вашего запроса\n\n" +
                "Подробности ошибки:\n" +
                "Введен некорректный URL адрес: '" + extractUrlFromErrorMessage(originalMessage) + "'\n\n" +
                "Пожалуйста, убедитесь, что:\n" +
                "1. Вы ввели полный URL адрес.\n" +
                "2. URL начинается с 'http://' или 'https://'.\n" +
                "3. URL не содержит недопустимых символов.\n\n" +
                "Пример правильного URL: https://www.example.com\n\n" +
                "Если вы уверены, что ввели корректный адрес, но ошибка повторяется, пожалуйста, обратитесь в службу поддержки.";

        DownloadResult errorResult = new DownloadResult(false, userFriendlyMessage, 0);
        errorResult.addError(originalMessage);

        model.addAttribute("result", errorResult);
        return "error";
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object  handleMissingParams(MissingServletRequestParameterException e, Model model) {
        log.error("Missing required parameter", e);
        return createErrorResponse("Отсутствует обязательный параметр: " + e.getParameterName(), e.getMessage(), model);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleTypeMismatch(MethodArgumentTypeMismatchException e, Model model) {
        log.error("Type mismatch for parameter", e);
        return createErrorResponse("Неверный формат параметра: " + e.getName(), e.getMessage(), model);
    }

    private String extractUrlFromErrorMessage(String errorMessage) {
        int start = errorMessage.indexOf("'");
        int end = errorMessage.indexOf("'", start + 1);
        if (start != -1 && end != -1) {
            return errorMessage.substring(start + 1, end);
        }
        return "неизвестный URL";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        log.error("Requested page not found", ex);
        String errorMessage = "Запрашиваемая страница не найдена. Пожалуйста, проверьте правильность URL.";
        return createErrorResponse(errorMessage, ex.getMessage(), model);
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleAllUncaughtException(Exception ex, Model model) {
        log.error("Unknown error occurred", ex);
        String errorMessage = "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже или обратитесь в поддержку.";
        return createErrorResponse(errorMessage, ex.getMessage(), model);
    }

    private Object createErrorResponse(String userMessage, String technicalMessage, Model model) {
        DownloadResult errorResult = new DownloadResult(false, userMessage, 0, Collections.singletonList(technicalMessage));

        if (model != null) {
            // Для веб-контроллера
            model.addAttribute("result", errorResult);
            return "error";  // Изменено с "download" на "error"
        } else {
            // Для REST контроллера
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }
}