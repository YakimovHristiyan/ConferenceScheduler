package com.example.conferencescheduler.model.dtos.conferenceDTOs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ConferenceDTO {

    private int conferenceId;
    private String conferenceName;
    private String description;
    private String address;
    private LocalDateTime startDate;
    private int ownerId;

}
