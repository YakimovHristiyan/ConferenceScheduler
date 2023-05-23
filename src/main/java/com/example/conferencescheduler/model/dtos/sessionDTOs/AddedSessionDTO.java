package com.example.conferencescheduler.model.dtos.sessionDTOs;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class AddedSessionDTO {

    private int conferenceId;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer hallId;

}
