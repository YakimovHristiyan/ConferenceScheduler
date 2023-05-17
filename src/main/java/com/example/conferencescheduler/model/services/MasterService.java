package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class MasterService {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected SpeakerRepository speakerRepository;
    @Autowired
    protected SessionRepository sessionRepository;
    @Autowired
    protected HallRepository hallRepository;
    @Autowired
    protected ConferenceRepository conferenceRepository;
}
