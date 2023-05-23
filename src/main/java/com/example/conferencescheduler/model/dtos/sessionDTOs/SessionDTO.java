package com.example.conferencescheduler.model.dtos.sessionDTOs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SessionDTO {

    private int conferenceId;
    private String name;
    private String description;
    private List<LocalTime> bookedHours;
    private LocalDateTime endDate;
    private Integer hallId;
}
