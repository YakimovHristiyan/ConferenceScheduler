package com.example.conferencescheduler.model.dtos.hallDTOs;

import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class HallWithSessionsDTO {

    private String hallName;
    private Integer capacity;
    private List<Session> sessions;
}
