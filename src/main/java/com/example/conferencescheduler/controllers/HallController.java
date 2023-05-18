package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.hallDTOs.CreateHallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallDTO;
import com.example.conferencescheduler.model.entities.Hall;
import com.example.conferencescheduler.model.services.HallService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class HallController extends AbstractController {

    @Autowired
    private HallService hallService;

    @PostMapping("/hall/{cid}/{hid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HallDTO addHallToConference(@PathVariable int hid, @PathVariable int cid, HttpSession session) {
        int userId = getUserId(session);
        return hallService.addHallToConference(userId, hid, cid);
    }

    @PostMapping("/hall")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HallDTO createHall(@RequestBody CreateHallDTO hall, HttpSession session){
        int userId = getUserId(session);
        return hallService.createHall(hall, userId);
    }
}
