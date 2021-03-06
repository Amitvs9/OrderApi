package com.vz.orderapi.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * ApiError
 * class to represent exceptions caught by ControllerAdvice {@link OrderExceptionHandler}
 */
@RequiredArgsConstructor
@Getter
public class ApiError {
    private final HttpStatus status;
    private final String message;
}
