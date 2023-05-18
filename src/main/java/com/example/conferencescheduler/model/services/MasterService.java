package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public abstract class MasterService {
    protected final static int USER_ROLE = 1;
    protected final static int SPEAKER_ROLE = 2;
    protected final static int CONFERENCE_OWNER_ROLE = 3;
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
    @Autowired
    protected PasswordEncoder encoder;
    @Autowired
    protected ModelMapper modelMapper;
}
