package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.sessionDTOs.AddedSessionDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.services.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
public class SessionController extends AbstractController {

    @Autowired
    private SessionService sessionService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public AddedSessionDTO addSession(@RequestBody SessionDTO dto, HttpSession httpSession) {
        int userId = getUserId(httpSession);
        return sessionService.addSession(dto, userId);
    }

    @DeleteMapping("/{sid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SessionDTO deleteSession(@PathVariable int sid, HttpSession session) {
        int userId = getUserId(session);
        return sessionService.deleteSession(userId, sid);
    }

    @PutMapping("/{sid}/{speakerId}")
    @ResponseStatus(code = HttpStatus.OK)
    public String assignSpeakerToSession(@PathVariable int sid, @PathVariable int speakerId, HttpSession session){
        int userId = getUserId(session);
        return sessionService.assignSpeakerToSession(userId, sid, speakerId);
    }

}
