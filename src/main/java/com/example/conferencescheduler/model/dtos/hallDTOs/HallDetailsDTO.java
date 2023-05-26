package com.example.conferencescheduler.model.dtos.hallDTOs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class HallDetailsDTO {

    private String hallName;
    private int bookedSeats;
    private int capacity;
}
