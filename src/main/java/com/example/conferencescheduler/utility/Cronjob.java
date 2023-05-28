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

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class Cronjob {
    @Autowired
    ConferenceRepository conferenceRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    StatusRepository statusRepository;

    @Scheduled(cron = "0 0 1 * * *")
    public void deleteAllSessionsFromNotActiveConferences() {
        List<Conference> conferences = conferenceRepository.findAll().stream()
                .filter(conference -> conference.getStatus().getConferenceStatusId() != MasterService.ACTIVE_STATUS).toList();

        conferences.forEach(conference -> conference.getSessions().removeAll(conference.getSessions()));
        conferenceRepository.saveAll(conferences);
        conferences.forEach(conference -> sessionRepository.deleteAllByConference_ConferenceId(conference.getConferenceId()));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void setConferencesInactive(){
        List<Conference> conferences = conferenceRepository.findAll().stream()
                .filter(conference -> conference.getEndDate().isBefore(LocalDateTime.now())).toList();

        conferences.forEach(conference -> conference.setStatus(statusRepository.getReferenceById(MasterService.INACTIVE_STATUS)));
        conferenceRepository.saveAll(conferences);
    }
}
