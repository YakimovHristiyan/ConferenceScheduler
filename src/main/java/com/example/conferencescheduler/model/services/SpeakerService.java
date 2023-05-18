package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.speakerDTOs.SpeakerRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserWithoutPassDTO;
import com.example.conferencescheduler.model.entities.Speaker;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SpeakerService extends MasterService{

    @Transactional
    public UserWithoutPassDTO register(SpeakerRegisterDTO dto) {
//        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        UserRegisterDTO userRegisterDTO = modelMapper.map(dto, UserRegisterDTO.class);
        validateUserInformation(userRegisterDTO);
        User user = modelMapper.map(dto, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRegisterAt(LocalDateTime.now());
        userRepository.save(user);

        Speaker speaker = new Speaker();
        speaker.setProfilePhoto(dto.getProfilePhoto());
        speaker.setDescription(dto.getDescription());
        speakerRepository.save(speaker);

        return modelMapper.map(user, UserWithoutPassDTO.class);
    }

}
