package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.sessionDTOs.AddedSessionDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.services.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SessionController extends AbstractController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/session")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public AddedSessionDTO addSession(@RequestBody SessionDTO dto, HttpSession httpSession) {
        int userId = getUserId(httpSession);
        return sessionService.addSession(dto, userId);
    }

    @DeleteMapping("/session/{sid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SessionDTO deleteSession(@PathVariable int sid, HttpSession session) {
        int userId = getUserId(session);
        return sessionService.deleteSession(userId, sid);
    }

}
