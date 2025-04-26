package com.example.userapi.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;

public class SuccessResponse<T> {
    private int status;
    private String message;
    private T data;
    private String timestamp;

    public SuccessResponse(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Getters
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public String getTimestamp() { return timestamp; }
}