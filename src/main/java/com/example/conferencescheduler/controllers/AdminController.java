package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import com.example.conferencescheduler.model.services.AdminService;
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

}
