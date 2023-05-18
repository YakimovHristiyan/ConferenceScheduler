package com.example.conferencescheduler.model.exceptions;


public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String msg){
        super(msg);
    }
}
