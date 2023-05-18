package com.example.conferencescheduler.model.dtos.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDTO {
    private int id;
    private String email;
    private String password;
}
