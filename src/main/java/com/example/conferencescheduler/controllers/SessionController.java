package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @Autowired
    private SessionService sessionService;
}
