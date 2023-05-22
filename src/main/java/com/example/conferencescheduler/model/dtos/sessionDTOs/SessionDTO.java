package com.example.conferencescheduler.model.dtos.sessionDTOs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class SessionDTO {

    private int conferenceId;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer hallId;
}
