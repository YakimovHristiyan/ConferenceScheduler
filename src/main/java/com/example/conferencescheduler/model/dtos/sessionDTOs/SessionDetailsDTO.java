package com.example.conferencescheduler.model.dtos.sessionDTOs;

import com.example.conferencescheduler.model.dtos.hallDTOs.HallDetailsDTO;
import com.example.conferencescheduler.model.dtos.speakerDTOs.SpeakerDetailsDTO;
import com.example.conferencescheduler.model.entities.Hall;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SessionDetailsDTO {

    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String hallName;
    private String speakerName;
    private int takenSeats;
    private int allSeats;

}
