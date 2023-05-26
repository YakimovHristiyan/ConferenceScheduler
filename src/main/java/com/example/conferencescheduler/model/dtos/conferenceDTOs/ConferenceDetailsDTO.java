package com.example.conferencescheduler.model.dtos.conferenceDTOs;

import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDetailsDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class ConferenceDetailsDTO {

    private List<SessionDetailsDTO> sessionDetailsDTOS;

}
