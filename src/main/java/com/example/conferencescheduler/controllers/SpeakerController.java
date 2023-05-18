package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.speakerDTOs.SpeakerRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserLoginDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserWithoutPassDTO;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.services.SpeakerService;
import com.example.conferencescheduler.model.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpeakerController extends AbstractController{

    @Autowired
    private SpeakerService speakerService;
    @Autowired
    private UserService userService;

    @PostMapping("/speakers")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserWithoutPassDTO register(@RequestBody SpeakerRegisterDTO dto){
        return speakerService.register(dto);
    }

}
