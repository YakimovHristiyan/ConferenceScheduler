package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.sessionDTOs.AddedSessionDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Hall;
import com.example.conferencescheduler.model.entities.Session;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import com.google.api.client.util.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService extends MasterService {

    @Transactional
    public AddedSessionDTO addSession(SessionDTO dto, int userId) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        User user = getUserById(userId);
        Conference conference = getConferenceById(dto.getConferenceId());
        Hall hall = getHallById(dto.getHallId());
        validateOwnerRights(conference, user);

        List<LocalTime> sortedHours = sortBookedHours(dto.getBookedHours());
        Session session = Session.builder()
                .name(dto.getName())
                .conference(conference)
                .description(dto.getDescription())
                .hall(hall)
                .startDate(LocalDateTime.of(conference.getStartDate().toLocalDate(), sortedHours.get(0)))
                .endDate(LocalDateTime.of(conference.getStartDate().toLocalDate(), sortedHours.get(sortedHours.size() - 1)))
                .build();

        List<Session> bookedSessions = hall.getSessions();
        iterateSessionAndCheckIfTheHoursFree(bookedSessions, session);

        if(conference.getEndDate().isBefore(session.getEndDate())){
        conference.setEndDate(session.getEndDate());
        conferenceRepository.save(conference);
        }

        sessionRepository.save(session);
        hall.getSessions().add(session);
        hallRepository.save(hall);
        return modelMapper.map(session, AddedSessionDTO.class);
    }

    private void iterateSessionAndCheckIfTheHoursFree(List<Session> bookedSessions, Session sessionWithWantedHours) {
        LocalDateTime startTime = sessionWithWantedHours.getStartDate();
        LocalDateTime endTime = sessionWithWantedHours.getEndDate();
        boolean isHourFree = false;
        for (Session bookedSession : bookedSessions) {
            LocalDateTime startTimeOfExistingSession = bookedSession.getStartDate();
            LocalDateTime endTimeOfExistingSession = bookedSession.getEndDate();
            if ((startTime.isBefore(startTimeOfExistingSession) && endTime.isBefore(startTimeOfExistingSession))
                    || (startTime.isAfter(endTimeOfExistingSession) && endTime.isAfter(endTimeOfExistingSession))) {
                isHourFree = true;
            }
        }
        if(!isHourFree){
            throw new BadRequestException("Time frame is not free!");
        }
    }

    private List<LocalTime> sortBookedHours(List<LocalTime> bookedHours) {
        bookedHours.sort(Comparator.comparingInt(LocalTime::getHour));
        return bookedHours;
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

    private void validateOwnerRights(Conference conference, User user) {
        if (user.getUserRole().getRoleId() != CONFERENCE_OWNER_ROLE || conference.getOwner().getUserId() != user.getUserId()) {
            throw new UnauthorizedException("You are not owner of this conference!");
        }
    }
}
