package com.example.userapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private List<FieldError> errors;
    private long timestamp;

    public ErrorResponse(int badRequest, String message) {
        this.status = badRequest;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(int badRequest, String message, List<FieldError> fieldErrors) {
        this(badRequest, message);
        this.errors = fieldErrors;
    }

	public ErrorResponse(HttpStatus badRequest, String string, List<FieldError> fieldErrors) {
		// TODO Auto-generated constructor stub
	}

	public ErrorResponse(HttpStatus status2, String message2) {
		// TODO Auto-generated constructor stub
	}
}