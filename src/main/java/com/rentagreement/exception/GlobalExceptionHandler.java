package com.rentagreement.exception;

import com.rentagreement.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException ex){

        ApiResponse<?> response =
                new ApiResponse<>(false, ex.getMessage(), null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex){

        ApiResponse<?> response =
                new ApiResponse<>(false, ex.getMessage(), null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidCredentials(
            InvalidCredentialsException ex
    ) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ApiResponse<>(
                                false,
                                ex.getMessage(),
                                null
                        )
                );
    }

}