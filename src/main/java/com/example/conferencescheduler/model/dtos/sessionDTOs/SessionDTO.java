package com.example.conferencescheduler.model.dtos.sessionDTOs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SessionDTO {

    private int conferenceId;
    private String name;
    private String description;
    private List<LocalDateTime> bookedHours;
    private LocalDateTime endDate;
    private Integer hallId;
}
