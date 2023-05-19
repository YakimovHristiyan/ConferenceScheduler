package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class ConferenceService extends MasterService {

    public ConferenceDTO publishConference(ConferenceDTO conferenceDTO, int userId) {
        if (findByIdAndReturnRole(userId) != CONFERENCE_OWNER_ROLE) {
            throw new UnauthorizedException("You do not have permission to publish conferences!");
        }
        //TODO Make a check if the chosen has day available halls and hours
        Conference conference = modelMapper.map(conferenceDTO, Conference.class);
        conferenceRepository.save(conference);
        return modelMapper.map(conference, ConferenceDTO.class);
    }


    public ConferenceDTO editConference(ConferenceDTO conferenceDTO, int userId) {
        if (userId != conferenceDTO.getOwnerId()) {
            throw new UnauthorizedException("You do not have permission to edit this conference!");
        }
        //TODO Make a check if the chosen day has available halls and hours
        Conference conference = modelMapper.map(conferenceDTO, Conference.class);
        conferenceRepository.save(conference);
        return modelMapper.map(conference, ConferenceDTO.class);
    }

    public ConferenceDTO deleteConference(int conferenceId, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new NotFoundException("Conference not found!"));
        if (user.getUserId() != conference.getOwner().getUserId()) {
            throw new UnauthorizedException("You can not delete other owners conferences!");
        }
        //Todo Delete all sessions for this conference
        conferenceRepository.delete(conference);
        return modelMapper.map(conference, ConferenceDTO.class);
    }

    public int findByIdAndReturnRole(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found!"))
                .getUserRole().getRoleId();

    }

}
