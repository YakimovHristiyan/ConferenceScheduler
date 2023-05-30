package com.example.conferencescheduler.utility;

import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.repositories.ConferenceRepository;
import com.example.conferencescheduler.model.repositories.SessionRepository;
import com.example.conferencescheduler.model.repositories.StatusRepository;
import com.example.conferencescheduler.model.services.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class Cronjob {
    @Autowired
    ConferenceRepository conferenceRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    StatusRepository statusRepository;

    @Transactional
    @Scheduled(cron = "1 * * * * *")
    public void deleteAllSessionsFromNotActiveConferences() {
        List<Conference> conferences = conferenceRepository.findAll().stream()
                .filter(conference -> conference.getStatus().getConferenceStatusId() != MasterService.ACTIVE_STATUS
                        && conference.getStatus().getConferenceStatusId() != MasterService.FUTURE_STATUS)
                .collect(Collectors.toList());

        conferences.forEach(conference -> conference.getSessions().clear());
        conferences.forEach(conference -> sessionRepository.deleteAllByConference_ConferenceId(conference.getConferenceId()));
        conferenceRepository.saveAll(conferences);
    }

    @Scheduled(cron = "1 * * * * *")
    public void setConferencesInactive() {
        List<Conference> conferences = conferenceRepository.findAll().stream()
                .filter(conference -> (conference.getEndDate() == null) ||
                        (conference.getEndDate().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())))
                .filter(conference -> conference.getStatus().getConferenceStatusId() != MasterService.INACTIVE_STATUS)
                .collect(Collectors.toList());

        conferences.forEach(conference -> conference.setStatus(statusRepository.getReferenceById(MasterService.INACTIVE_STATUS)));
        conferenceRepository.saveAll(conferences);
    }

    @Scheduled(cron = "1 * * * * *")
    public void setConferencesActive() {
        List<Conference> conferences = conferenceRepository.findAll().stream()
                .filter(conference -> conference.getStartDate().toLocalDate().isEqual(LocalDateTime.now().toLocalDate()))
                .collect(Collectors.toList());

        conferences.forEach(conference -> conference.setStatus(statusRepository.getReferenceById(MasterService.ACTIVE_STATUS)));
        conferenceRepository.saveAll(conferences);
    }
}
