package com.example.conferencescheduler.model.dtos.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditUserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
