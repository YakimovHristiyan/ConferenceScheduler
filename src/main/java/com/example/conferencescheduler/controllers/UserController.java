package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserWithoutPassDTO;
import com.example.conferencescheduler.model.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends AbstractController{

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserWithoutPassDTO register(@RequestBody UserRegisterDTO dto){
        return userService.register(dto);
    }

    @GetMapping(value = "/users/email-verification")
    public String verifyEmail(HttpSession session) {
        int uid = getUserId(session);
        return userService.verifyEmail(uid);
    }
}
