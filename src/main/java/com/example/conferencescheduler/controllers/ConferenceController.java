package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.conferenceDTOs.AssignConferenceDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.EditConferenceDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.SessionDTO;
import com.example.conferencescheduler.model.services.ConferenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ConferenceController extends AbstractController {

    @Autowired
    private ConferenceService conferenceService;

    @PostMapping("/conference")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ConferenceDTO publishConference(@RequestBody ConferenceDTO conferenceDTO, HttpSession session) {
        int id = getUserId(session);
        return conferenceService.publishConference(conferenceDTO, id);
    }

    @PutMapping("/conference/{confid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public EditConferenceDTO editConference(@PathVariable int confid, @RequestBody EditConferenceDTO dto, HttpSession session) {
        int id = getUserId(session);
        return conferenceService.editConference(dto, id, confid);
    }

    @DeleteMapping("/conference/{cid}")//TODO This can be done only from admin, to be moved in admin controller
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ConferenceDTO deleteConference(@PathVariable int cid, HttpSession session) {
        int id = getUserId(session);
        return conferenceService.deleteConference(cid, id);
    }

    @GetMapping("/conference")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ConferenceDTO> getAllConferences() {
        return conferenceService.getAllConferences();
    }

    @GetMapping("/conference/{cid}")
    @ResponseStatus(code = HttpStatus.OK)
    public ConferenceDTO viewConference(@PathVariable int cid) {
        return conferenceService.viewConference(cid);
    }

    @GetMapping("/session/all-conference-sessions/{cid}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<SessionDTO> getConferenceAllSessions(@PathVariable int cid){
        return conferenceService.getConferenceAllSessions(cid);
    }

}
