package com.example.project.exception;

import com.example.project.exception.response.ErrorResponse;
import com.example.project.exception.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(buildErrorResponse(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации данных",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Параметр '%s' имеет неверное значение '%s'", ex.getName(), ex.getValue());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Неверный формат JSON-запроса"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера"));
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        return new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now()
        );
    }
}