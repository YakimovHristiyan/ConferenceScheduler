package com.example.conferencescheduler.model.dtos.conferenceDTOs;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DateTimeDTO {

    private LocalDate date;
    private LocalTime time;
}
