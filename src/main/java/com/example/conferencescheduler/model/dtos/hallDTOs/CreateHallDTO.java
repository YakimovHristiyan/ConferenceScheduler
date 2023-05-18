package com.example.conferencescheduler.model.dtos.hallDTOs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateHallDTO {

    private int hallId;
    private String hallName;
    private Integer capacity;
}
