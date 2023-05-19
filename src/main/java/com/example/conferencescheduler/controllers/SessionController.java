package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.services.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SessionController extends AbstractController{

    @Autowired
    private SessionService sessionService;

    @PostMapping("/session/{cid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SessionDTO addSession(@RequestBody SessionDTO sessionDTO, @PathVariable int cid, HttpSession httpSession){
        int userId = getUserId(httpSession);
        return sessionService.addSession(sessionDTO, userId, cid);
    }
    @DeleteMapping("/session/{sid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SessionDTO deleteSession(@PathVariable int sid, HttpSession httpSession){
        int userId = getUserId(httpSession);
        return sessionService.deleteSession(userId, sid);
    }

}
