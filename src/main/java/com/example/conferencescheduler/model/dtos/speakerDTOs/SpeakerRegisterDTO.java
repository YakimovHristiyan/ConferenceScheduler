package com.example.conferencescheduler.model.dtos.speakerDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpeakerRegisterDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private String phone;
    private int roleId;
    private String profilePhoto;
    private String description;
}
