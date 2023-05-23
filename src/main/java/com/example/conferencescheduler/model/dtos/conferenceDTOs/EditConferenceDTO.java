package com.example.conferencescheduler.model.dtos.conferenceDTOs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class EditConferenceDTO {

    private String conferenceName;
    private String description;
    private String address;
    private LocalDateTime startDate;

}
