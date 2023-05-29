package com.example.conferencescheduler.model;

import com.example.conferencescheduler.model.repositories.*;
import com.example.conferencescheduler.model.services.MasterService;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class MasterTest {
    @Mock
    protected JavaMailSender emailSender;
    @Mock
    protected UserRepository userRepository;
    @Mock
    protected SpeakerRepository speakerRepository;
    @Mock
    protected SessionRepository sessionRepository;
    @Mock
    protected HallRepository hallRepository;
    @Mock
    protected ConferenceRepository conferenceRepository;
    @Mock
    protected PasswordEncoder encoder;
    @Mock
    protected ModelMapper modelMapper;
    @Mock
    protected UserRoleRepository userRoleRepository;
    @Mock
    protected StatusRepository statusRepository;
}
