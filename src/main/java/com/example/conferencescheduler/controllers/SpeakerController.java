package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.services.SpeakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpeakerController extends AbstractController{

    @Autowired
    private SpeakerService speakerService;
}
