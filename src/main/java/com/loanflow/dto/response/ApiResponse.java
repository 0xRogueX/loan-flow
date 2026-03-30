package com.loanflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success;
    private final int statusCode; // Added status code field
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    // Standard 200 OK without a custom message
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, 200, "Success", data, LocalDateTime.now());
    }

    // Standard 200 OK with a custom message
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, 200, message, data, LocalDateTime.now());
    }

    // Standard 201 CREATED for new resources (like loan applications)
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, 201, message, data, LocalDateTime.now());
    }

    // Error response allowing dynamic status codes (404, 500, etc.)
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(false, statusCode, message, null, LocalDateTime.now());
    }

    // Standard 400 Bad Request for validation errors
    public static <T> ApiResponse<T> validationError(T errors) {
        return new ApiResponse<>(false, 400, "Validation failed", errors, LocalDateTime.now());
    }
}