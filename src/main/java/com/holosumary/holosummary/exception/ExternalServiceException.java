package com.holosumary.holosummary.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExternalServiceException extends RuntimeException {
    private final HttpStatus status;
    private final String responseBody;

    public ExternalServiceException(String message) {
        this(message, HttpStatus.BAD_GATEWAY, null);
    }

    public ExternalServiceException(String message, HttpStatus status) {
        this(message, status, null);
    }

    public ExternalServiceException(String message, HttpStatus status, String responseBody) {
        super(message);
        this.status = status == null ? HttpStatus.BAD_GATEWAY : status;
        this.responseBody = responseBody;
    }

}
