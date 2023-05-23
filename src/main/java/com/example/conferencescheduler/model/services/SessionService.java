package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.sessionDTOs.AddedSessionDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Hall;
import com.example.conferencescheduler.model.entities.Session;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService extends MasterService {

    @Transactional
    public AddedSessionDTO addSession(SessionDTO dto, int userId) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        User user = getUserById(userId);
        Conference conference = getConferenceById(dto.getConferenceId());
        Hall hall = getHallById(dto.getHallId());
        validateOwnerRights(conference, user);
        Session session = Session.builder()
                .name(dto.getName())
                .conference(conference)
                .description(dto.getDescription())
                .hall(hall)
                .startDate(LocalDateTime.of(conference.getStartDate().toLocalDate(), dto.getBookedHours().get(0)))
                .endDate(LocalDateTime.of(conference.getStartDate().toLocalDate(), dto.getBookedHours().get(dto.getBookedHours().size() - 1)))
                .build();
        //Todo check if the hour is free to add this session
        sessionRepository.save(session);
        //TODO if we want to set the end date time for the conference we need to get all the sessions and check which one is with the latest hour.
        hall.getSessions().add(session);
        hallRepository.save(hall);
        return modelMapper.map(session, AddedSessionDTO.class);
    }

    public SessionDTO deleteSession(int userId, int sessionId) {
        User user = getUserById(userId);
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new NotFoundException("Session not found!"));
        Conference conference = getConferenceById(session.getConference().getConferenceId());

        validateOwnerRights(conference, user);

        //Todo Notify all guests that the session is removed
        sessionRepository.delete(session);
        return modelMapper.map(session, SessionDTO.class);
    }

    public void validateOwnerRights(Conference conference, User user) {
        if (user.getUserRole().getRoleId() != CONFERENCE_OWNER_ROLE || conference.getOwner().getUserId() != user.getUserId()) {
            throw new UnauthorizedException("You are not owner of this conference!");
        }
    }
}
