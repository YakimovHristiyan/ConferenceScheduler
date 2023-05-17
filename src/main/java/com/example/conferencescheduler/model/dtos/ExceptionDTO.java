package com.example.conferencescheduler.model.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@RequiredArgsConstructor
public class ExceptionDTO {
    private int status;
    private LocalDateTime dateTime;
    private String msg;
}
