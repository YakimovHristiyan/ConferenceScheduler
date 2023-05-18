package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class ConferenceService extends MasterService {

    public ConferenceDTO publishConference(ConferenceDTO conferenceDTO, int id) {
        int roleId = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found!")).getUserRole().getRoleId();
        if (roleId != CONFERENCE_OWNER_ROLE){
            throw new UnauthorizedException("You do not have permission to publish conferences!");
        }

        Conference conference = modelMapper.map(conferenceDTO, Conference.class);
        conferenceRepository.save(conference);
        return modelMapper.map(conference, ConferenceDTO.class);
    }
}
