package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.hallDTOs.HallWithSessionsDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.services.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SessionController extends AbstractController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/session")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SessionDTO addSession(@RequestBody SessionDTO sessionDTO, HttpSession httpSession){
        int userId = getUserId(httpSession);
        return sessionService.addSession(sessionDTO, userId);
    }

    @DeleteMapping("/session/{sid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SessionDTO deleteSession(@PathVariable int sid, HttpServletRequest request) {
        int userId = getLoggedUserId(request);
        return sessionService.deleteSession(userId, sid);
    }

    @GetMapping("/session/all-conference-sessions/{cid}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<SessionDTO> getConferenceAllSessions(@PathVariable int cid){
        return sessionService.getConferenceAllSessions(cid);
    }

}
