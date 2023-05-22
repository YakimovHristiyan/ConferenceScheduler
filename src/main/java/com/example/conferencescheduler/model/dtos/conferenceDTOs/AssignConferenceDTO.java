package com.example.conferencescheduler.model.dtos.conferenceDTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignConferenceDTO {

    private int conferenceId;
    private int hallId;
    private LocalDateTime conferenceStartDate;

}
