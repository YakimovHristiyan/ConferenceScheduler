package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDetailsDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceWithStatusDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.EditConferenceDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDetailsDTO;
import com.example.conferencescheduler.model.dtos.speakerDTOs.SpeakerDetailsDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Session;
import com.example.conferencescheduler.model.entities.Status;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import lombok.Builder;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<ConferenceWithStatusDTO> getAllConferences() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<Conference> conferences = conferenceRepository.findAll();
        List<ConferenceWithStatusDTO> conferenceWithStatusDTOS = new ArrayList<>();
        for (int i = 0; i < conferences.size(); i++) {
            conferenceWithStatusDTOS.add(modelMapper.map(conferences.get(i), ConferenceWithStatusDTO.class));
            conferenceWithStatusDTOS.get(i).setConferenceStatus(conferences.get(i).getStatus().getType());
        }
        return conferenceWithStatusDTOS;
    }

    public ConferenceDetailsDTO viewConference(int cid) {
        Conference conference = getConferenceById(cid);
        List<Session> sessions = conference.getSessions();
        List<SessionDetailsDTO> list = conference.getSessions()
                .stream()
                .map(session -> modelMapper.map(session, SessionDetailsDTO.class))
                .toList();
        for (int i = 0; i < sessions.size(); i++) {
            StringBuilder speakerName = new StringBuilder()
                    .append(sessions.get(i).getSpeaker().getUser().getFirstName() + " " + sessions.get(i).getSpeaker().getUser().getLastName());
            list.get(i).setSpeakerName(speakerName.toString());
            list.get(i).setHallName(sessions.get(i).getHall().getHallName());
        }
        ConferenceDetailsDTO conf = modelMapper.map(conference, ConferenceDetailsDTO.class);
        conf.setSessionDetailsDTOS(list);
        return conf;
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
