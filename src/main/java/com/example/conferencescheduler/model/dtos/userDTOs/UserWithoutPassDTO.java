package com.example.conferencescheduler.model.dtos.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserWithoutPassDTO {
    private int id;
    private String firstName;
    private String lastName;
    private int roleId;
}
