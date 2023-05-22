package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.conferenceDTOs.AssignConferenceDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Hall;
import com.example.conferencescheduler.model.entities.Session;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConferenceService extends MasterService {

    public ConferenceDTO publishConference(ConferenceDTO conferenceDTO, int userId) {
        if (getUserById(userId).getUserRole().getRoleId() != CONFERENCE_OWNER_ROLE) {
            throw new UnauthorizedException("You do not have permission to publish conferences!");
        }
        Conference conference = modelMapper.map(conferenceDTO, Conference.class);
        conferenceRepository.save(conference);
        return modelMapper.map(conference, ConferenceDTO.class);
    }


    public ConferenceDTO editConference(ConferenceDTO conferenceDTO, int userId) {
        if (userId != conferenceDTO.getOwnerId()) {
            throw new UnauthorizedException("You do not have permission to edit this conference!");
        }
        Conference conference = modelMapper.map(conferenceDTO, Conference.class);
        conferenceRepository.save(conference);
        return modelMapper.map(conference, ConferenceDTO.class);
    }

    public ConferenceDTO deleteConference(int conferenceId, int userId) {
        User user = getUserById(userId);
        Conference conference = getConferenceById(conferenceId);
        if (user.getUserId() != conference.getOwner().getUserId()) {
            throw new UnauthorizedException("You can not delete other owners conferences!");
        }
        // Todo Delete all sessions for this conference
        conferenceRepository.delete(conference);
        return modelMapper.map(conference, ConferenceDTO.class);
    }

    public List<ConferenceDTO> getAllConferences() {
        List<Conference> conferences = conferenceRepository.findAll();
        return conferences.stream().map(e -> modelMapper.map(e, ConferenceDTO.class)).collect(Collectors.toList());
    }

    public ConferenceDTO viewConference(int id) {
        return modelMapper.map(getConferenceById(id), ConferenceDTO.class);
    }

    @Transactional
    public AssignConferenceDTO assignConferenceToHall(AssignConferenceDTO dto) {
        Hall hall = hallRepository.findByHallId(dto.getHallId())
                .orElseThrow(() -> new NotFoundException("This hall does not exist."));
        Conference conference = conferenceRepository.findByConferenceId(dto.getConferenceId())
                .orElseThrow(() -> new NotFoundException("This conference does not exist."));
        List<Session> hallSession = hall.getSessions();
        for(Session s : hallSession){
            if (s.getStartDate().isEqual(dto.getConferenceStartDate())){
                throw new BadRequestException("Time is not available.");
            }
        }
        //4. Add conference to the hall
        hall.getConferences().add(conference);
        conference.getHalls().add(hall);
        conference.setStartDate(dto.getConferenceStartDate());
        conferenceRepository.save(conference);
        hallRepository.save(hall);
        return dto;
    }
}
