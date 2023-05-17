package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.services.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConferenceController {

    @Autowired
    private ConferenceService conferenceService;
}
