package com.example.conferencescheduler.model.dtos.speakerDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpeakerDetailsDTO {

    private String firstName;
    private String lastName;
    private String profilePhoto;
    private String description;

}
