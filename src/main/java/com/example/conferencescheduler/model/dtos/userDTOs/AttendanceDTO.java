package com.example.conferencescheduler.model.dtos.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceDTO {
    private int conferenceId;
    private int sessionId;
}
