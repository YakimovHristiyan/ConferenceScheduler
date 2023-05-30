package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.hallDTOs.CreateHallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.DateDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallWithSessionsDTO;
import com.example.conferencescheduler.model.services.HallService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/halls")
public class HallController extends AbstractController {

    @Autowired
    private HallService hallService;

    @PutMapping("/{cid}/{hid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HallDTO addHallToConference(@PathVariable int cid, @PathVariable int hid, HttpSession session) {
        int userId = getUserId(session);
        return hallService.addHallToConference(userId, hid, cid);
    }

    @DeleteMapping("/{cid}/{hid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public String removeHallFromConference(@PathVariable int cid, @PathVariable int hid, HttpSession session) {
        int userId = getUserId(session);
        return hallService.removeHallFromConference(userId, hid, cid);
    }

    @GetMapping("/{hid}")
    @ResponseStatus(code = HttpStatus.OK)
    public HallWithSessionsDTO viewHall(@PathVariable int hid) {
        return hallService.viewHall(hid);
    }

    @GetMapping("/free-halls-slots")
    @ResponseStatus(code = HttpStatus.OK)
    public List<HallDTO> getAvailableTimeSlots(@RequestBody DateDTO dto) {
        return hallService.getAvailableTimeSlots(dto);
    }

    @GetMapping("/conference-halls/{cid}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CreateHallDTO> getAllConferenceHalls(@PathVariable int cid){
        return hallService.getAllConferenceHalls(cid);
    }

}
