package com.example.conferencescheduler.model.exceptions;


public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message){
        super(message);
    }
}
