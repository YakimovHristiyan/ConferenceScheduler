package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.sessionDTOs.AddedSessionDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.entities.*;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class SessionService extends MasterService {
    private final static int startingWorkingHour = 9;
    private final static int endingWorkingHour = 18;

    @Transactional
    public AddedSessionDTO addSession(SessionDTO dto, int userId) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        User user = getUserById(userId);
        Conference conference = getConferenceById(dto.getConferenceId());
        Hall hall = getHallById(dto.getHallId());
        if (!conference.getHalls().contains(getHallById(dto.getHallId()))) {
            throw new BadRequestException("This hall is not assigned to the conference!");
        }
        List<LocalTime> sortedHours = sortBookedHours(dto.getBookedHours());
        validateOwnerRights(conference, user);
        isWorkingHours(sortedHours);

        Session session = Session.builder()
                .name(dto.getName())
                .conference(conference)
                .description(dto.getDescription())
                .hall(hall)
                .startDate(LocalDateTime.of(conference.getStartDate().toLocalDate(), sortedHours.get(0)))
                .endDate(LocalDateTime.of(conference.getStartDate().toLocalDate(), sortedHours.get(sortedHours.size() - 1).plusMinutes(60)))
                .build();

        if (sortedHours.size() < 2) {
            session.setEndDate(session.getStartDate().plusMinutes(60));
        }
        List<Session> bookedSessions = hall.getSessions();
        bookedSessions.sort(Comparator.comparing(o -> o.getStartDate().toLocalTime()));

        if (!bookedSessions.isEmpty()) {
            iterateSessionAndCheckIfTheHoursFree(bookedSessions, session);
        }
        // Assigning end date and start date of conference based on the latest session
        setConferenceStartAndEnd(conference, session);

        sessionRepository.save(session);
        hall.getSessions().add(session);
        hallRepository.save(hall);
        return modelMapper.map(session, AddedSessionDTO.class);
    }

    private void setConferenceStartAndEnd(Conference conference, Session session) {
        if (conference.getStartDate().getHour() == 0) {
            conference.setStartDate(session.getStartDate());
            conferenceRepository.save(conference);
        } else if (conference.getStartDate().isAfter(session.getStartDate())) {
            conference.setStartDate(session.getStartDate());
            conferenceRepository.save(conference);
        }
        if (conference.getEndDate() == null) {
            conference.setEndDate(session.getEndDate());
            conferenceRepository.save(conference);
        } else if (conference.getEndDate().isBefore(session.getEndDate())) {
            conference.setEndDate(session.getEndDate());
            conferenceRepository.save(conference);
        }
    }

    private void isWorkingHours(List<LocalTime> sortedHours) {
        if (sortedHours.get(0).isBefore(LocalTime.of(startingWorkingHour, 0, 0))) {
            throw new BadRequestException("Booked time cannot be before 09:00:00");
        }
        if ((sortedHours.get(sortedHours.size() - 1)).isAfter(LocalTime.of(endingWorkingHour, 0, 0))
                || (sortedHours.get(sortedHours.size() - 1)).equals(LocalTime.of(endingWorkingHour, 0, 0))) {
            throw new BadRequestException("Booked time cannot be after 18:00:00");
        }
    }

    private void iterateSessionAndCheckIfTheHoursFree(List<Session> bookedSessions, Session sessionWithWantedHours) {
        boolean isHourFree = true;
        for (Session bookedSession : bookedSessions) {
            isHourFree = checkHoursAreFree(bookedSession, sessionWithWantedHours);
        }

        if (!isHourFree) {
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
        notifyAllUsers(session, user, conference);
        sessionRepository.delete(session);
        return modelMapper.map(session, SessionDTO.class);
    }

    private void notifyAllUsers(Session session, User user, Conference conference) {
        List<User> guests = session.getGuests();
        String subject = "Session \"" + session.getName() + "\" is deleted";
        String text = "Session \"" + session.getName() + "\" with starting date "
                + session.getStartDate() + " from conference \"" + conference.getConferenceName() + "\" was deleted!\n" +
                "You can see all other sessions with free place from this or other conferences!\n"
                + "Sorry for the misunderstanding. Have a nice day!\n";

        for (User guest : guests) {
            sendEmail(conference.getOwner().getEmail(), guest.getEmail(), subject, text);
        }
    }

    private void validateOwnerRights(Conference conference, User user) {
        if (user.getUserRole().getRoleId() != CONFERENCE_OWNER_ROLE || conference.getOwner().getUserId() != user.getUserId()) {
            throw new UnauthorizedException("You are not owner of this conference!");
        }
    }

    public String assignSpeakerToSession(int userId, int sid, int speakerId) {
        Session session = getSessionById(sid);

        Speaker speaker = getSpeakerById(speakerId);
        validateSpeakerAndOwner(session, userId, speaker);
        session.setSpeaker(speaker);
        sessionRepository.save(session);
        return "Speaker successfully added to the current session";
    }

    private void validateSpeakerAndOwner(Session session, int userId, Speaker speaker) {
        if (session.getConference().getOwner().getUserId() != userId) {
            throw new BadRequestException("You are not the owner of this conference.");
        }
        List<Session> speakerSessions = speaker.getSessions().stream()
                .filter(session1 -> session1.getStartDate().toLocalDate().isEqual(session.getStartDate().toLocalDate())).toList();
        boolean isNotColliding = true;
        for (Session speakerSession : speakerSessions) {
            isNotColliding = checkHoursAreFree(speakerSession, session);
            if(!isNotColliding){
                break;
            }
        }
        if (!isNotColliding) {
            throw new BadRequestException("You can not add speaker in two sessions with the same time frame!");
        }
    }
}
