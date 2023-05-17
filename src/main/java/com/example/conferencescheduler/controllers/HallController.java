package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.services.HallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HallController extends AbstractController{

    @Autowired
    private HallService hallService;
}
