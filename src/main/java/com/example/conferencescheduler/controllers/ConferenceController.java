package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceDetailsDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.ConferenceWithStatusDTO;
import com.example.conferencescheduler.model.dtos.conferenceDTOs.EditConferenceDTO;
import com.example.conferencescheduler.model.services.ConferenceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conferences")
public class ConferenceController extends AbstractController {

    @Autowired
    private ConferenceService conferenceService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ConferenceDTO publishConference(@RequestBody ConferenceDTO conferenceDTO, HttpSession session) {
        int id = getUserId(session);
        return conferenceService.publishConference(conferenceDTO, id);
    }

    @PutMapping("/{conferenceId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public EditConferenceDTO editConference(@PathVariable int conferenceId, @RequestBody EditConferenceDTO conferenceDTO, HttpSession session) {
        int id = getUserId(session);
        return conferenceService.editConference(conferenceDTO, id, conferenceId);
    }

    @PutMapping("/suspend/{cid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ConferenceDTO suspendConference(@PathVariable int cid, HttpSession session) {
        int id = getUserId(session);
        return conferenceService.suspendConference(cid, id);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ConferenceWithStatusDTO> getAllConferences() {
        return conferenceService.getAllConferences();
    }

    @GetMapping("/{cid}")
    @ResponseStatus(code = HttpStatus.OK)
    public ConferenceDetailsDTO viewConference(@PathVariable int cid) {
        return conferenceService.viewConference(cid);
    }

}
