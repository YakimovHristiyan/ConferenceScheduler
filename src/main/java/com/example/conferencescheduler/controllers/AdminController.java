package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.hallDTOs.CreateHallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallDTO;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import com.example.conferencescheduler.model.services.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController extends AbstractController {

    public static final String ADMIN_PASSWORD = "4kd2!kd7@SE1";

    @Autowired
    AdminService adminService;
    @PutMapping(value = "/change-user-role/{uid}", headers = "password=" + ADMIN_PASSWORD)
    @ResponseStatus(code = HttpStatus.OK)
    public String changeUserRoleToConferenceOwner(@PathVariable int uid) {
        return adminService.changeUserRoleToConferenceOwner(uid);
    }

    @PostMapping("/hall")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HallDTO createHall(@RequestBody CreateHallDTO hall, HttpSession session) {
        int userId = getUserId(session);
        return adminService.createHall(hall, userId);
    }

}
