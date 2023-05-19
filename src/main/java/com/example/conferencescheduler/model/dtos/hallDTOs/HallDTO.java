package com.example.conferencescheduler.model.dtos.hallDTOs;

import com.example.conferencescheduler.model.entities.Conference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

@Getter
@Setter
@RequiredArgsConstructor
public class HallDTO {

    private int hallId;
    private String hallName;
    private Integer capacity;
    private Integer conferenceId;
}
