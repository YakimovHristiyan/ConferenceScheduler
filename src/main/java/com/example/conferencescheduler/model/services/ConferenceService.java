package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.conferenceDTOs.AssignConferenceDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.EditConferenceDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
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
        User user = getUserById(userId);
        if (user.getUserRole().getRoleId() != CONFERENCE_OWNER_ROLE) {
            throw new UnauthorizedException("You do not have permission to publish conferences!");
        }
        if(conferenceRepository.getConferenceByConferenceName(conferenceDTO.getConferenceName()) != null){
            throw new BadRequestException("This name for conference is taken!");
        }
        Conference conference = modelMapper.map(conferenceDTO, Conference.class);
        conference.setStartDate(conferenceDTO.getStartDate().minusHours(conferenceDTO.getStartDate().getHour()));
        conference.setOwner(user);
        conferenceRepository.save(conference);
        return modelMapper.map(conference, ConferenceDTO.class);
    }

    public EditConferenceDTO editConference(EditConferenceDTO dto, int userId, int confId) {
        Conference conference = getConferenceById(confId);
        if (userId != conference.getOwner().getUserId()) {
            throw new UnauthorizedException("You do not have permission to edit this conference!");
        }
        conference.setConferenceName(dto.getConferenceName());
        conference.setDescription(dto.getDescription());
        conference.setAddress(dto.getAddress());
        conference.setStartDate(dto.getStartDate());
        conferenceRepository.save(conference);
        return modelMapper.map(conference, EditConferenceDTO.class);
    }

    public ConferenceDTO deleteConference(int conferenceId, int userId) {
        User user = getUserById(userId);
        Conference conference = getConferenceById(conferenceId);
        if (user.getUserId() != conference.getOwner().getUserId()) {
            throw new UnauthorizedException("You can not delete other owners conferences!");
        }
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

    public List<SessionDTO> getConferenceAllSessions(int cid) {
        Conference conference = conferenceRepository.findByConferenceId(cid)
                .orElseThrow(() -> new NotFoundException("Conference not found."));
        return conference.getSessions()
                .stream()
                .map(session -> modelMapper.map(session, SessionDTO.class))
                .toList();
    }
  
}
