package com.example.conferencescheduler.model.exceptions;

public class UnauthorizedException extends Exception{
    public UnauthorizedException(String message){
        super(message);
    }
}
