package com.example.userapi.dto;

public class FieldError {
	 private String field;
	 private String code;
	 private String message;

	 public FieldError(String field, String code, String message) {
	     this.field = field;
	     this.code = code;
	     this.message = message;
	 }

	}