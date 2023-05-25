package com.example.conferencescheduler.model.dtos.userDTOs;

import com.example.conferencescheduler.model.dtos.sessionDTOs.AddedSessionDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.entities.Session;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserWithSessionDTO {
    private int userId;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<AddedSessionDTO> sessions;
}
