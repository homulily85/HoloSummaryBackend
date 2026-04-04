package com.holosumary.holosummary.advice;

import com.holosumary.holosummary.controller.TranscriptController;
import com.holosumary.holosummary.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice(assignableTypes = TranscriptController.class)
public class TranscriptControllerAdvice {
    @ExceptionHandler({HttpClientErrorException.BadRequest.class, NotFoundException.class})
    public ResponseEntity<ErrorDetail> badRequestExceptionHandler() {
        return ResponseEntity.badRequest().body(new ErrorDetail("Video cannot be found in our " +
                "database or there is no transcript available!"));

    }

    @Data
    @AllArgsConstructor
    public static class ErrorDetail {
        String message;
    }
}
