package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Session;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class SessionService extends MasterService {
    public SessionDTO addSession(SessionDTO sessionDTO, int userId, int conferenceId) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        User user = getUserById(userId);
        Conference conference = getConferenceById(conferenceId);
        validateOwnerRights(conference, user);
        Session session = modelMapper.map(sessionDTO, Session.class);
        //Todo check if the hour is free to add this session
        session.setEndDate(session.getStartDate().plusMinutes(60));
        sessionRepository.save(session);
        conference.setEndDate(conference.getStartDate().plusMinutes(60));
        conferenceRepository.save(conference);
        return modelMapper.map(session, SessionDTO.class);
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
        if (user.getUserRole().getRoleId() != CONFERENCE_OWNER_ROLE || conference.getOwner().getUserId() != user.getUserId()){
            throw new UnauthorizedException("You are not owner of this conference!");
        }
    }

}
