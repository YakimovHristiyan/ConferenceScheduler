package com.example.conferencescheduler.model.exceptions;

public class BadRequestException extends Exception{
    public BadRequestException(String msg) {
        super(msg);
    }
    public BadRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
