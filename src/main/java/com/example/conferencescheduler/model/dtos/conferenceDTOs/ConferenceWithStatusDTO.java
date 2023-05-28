package com.example.conferencescheduler.model.dtos.conferenceDTOs;

import com.example.conferencescheduler.model.entities.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ConferenceWithStatusDTO {

    private int conferenceId;
    private String conferenceName;
    private String description;
    private String address;
    private LocalDateTime startDate;
    private String conferenceStatus;
    private int ownerId;

}
