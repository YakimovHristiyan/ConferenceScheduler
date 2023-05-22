package com.example.conferencescheduler.model.dtos.userDTOs;

import com.example.conferencescheduler.model.entities.Session;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserWithSessionDTO {
    private int id;
    @JsonIgnore
    private List<Session> sessions;
}
