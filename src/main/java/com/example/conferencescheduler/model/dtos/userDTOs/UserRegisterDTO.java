package com.example.conferencescheduler.model.dtos.userDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private String phone;
    private int roleId;
}
