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
public class HallController extends AbstractController {

    @Autowired
    private HallService hallService;

    @PostMapping("/hall/{cid}/{hid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HallDTO addHallToConference(@PathVariable int cid, @PathVariable int hid, HttpSession session) {
        int userId = getUserId(session);
        return hallService.addHallToConference(userId, hid, cid);
    }

    @DeleteMapping("/hall/{cid}/{hid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HallDTO removeHallFromConference(@PathVariable int cid, @PathVariable int hid, HttpSession session) {
        int userId = getUserId(session);
        return hallService.removeHallFromConference(userId, hid, cid);
    }

    @PostMapping("/hall")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HallDTO createHall(@RequestBody CreateHallDTO hall, HttpSession session){
        int userId = getUserId(session);
        return hallService.createHall(hall, userId);
    }

    @GetMapping("/hall/{hid}")
    @ResponseStatus(code = HttpStatus.OK)
    public HallWithSessionsDTO viewHall(@PathVariable int hid){
        return hallService.viewHall(hid);
    }

    @GetMapping("/hall/free-halls-slots")
    @ResponseStatus(code = HttpStatus.OK)
    public List<HallDTO> getAvailableTimeSlots(@RequestBody DateDTO dto){
        return hallService.getAvailableTimeSlots(dto);
    }
}
