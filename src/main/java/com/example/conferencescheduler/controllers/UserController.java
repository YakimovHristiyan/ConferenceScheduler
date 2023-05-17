package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends AbstractController{

    @Autowired
    private UserService userService;
}
