package com.example.winter_olympics.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
