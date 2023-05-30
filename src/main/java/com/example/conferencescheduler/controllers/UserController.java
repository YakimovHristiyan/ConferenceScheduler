package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.hallDTOs.DateDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.*;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.services.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@PermitAll
@RequestMapping("/users")
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserWithoutPassDTO register(@RequestBody UserRegisterDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/auth")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public UserWithoutPassDTO login(@RequestBody UserLoginDTO dto, HttpServletRequest request) {
        UserWithoutPassDTO result = userService.userLogin(dto);
        if (result != null) {
            logUser(request, result.getId());
            return result;
        } else {
            throw new BadRequestException("Wrong Credentials!");
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(code = HttpStatus.OK)
    public String logout(HttpSession session) {
        session.invalidate();
        return "You have successfully logged out!";
    }

    @PutMapping
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<EditUserDTO> editAccount(@RequestBody EditUserDTO newUser, HttpSession session) {
        int userId = getUserId(session);
        return ResponseEntity.ok(userService.editAccount(newUser, userId));
    }

    @GetMapping(value = "/email-verification/{uid}")
    public String verifyEmail(@PathVariable int uid) {
        return userService.verifyEmail(uid);
    }

    @PutMapping("/attendance")
    @ResponseStatus(code = HttpStatus.OK)
    public UserWithSessionDTO assertAttendance(@RequestBody AttendanceDTO attendanceDTO, HttpSession session){
        int userId = getUserId(session);
        return userService.assertAttendance(userId, attendanceDTO);
    }

    @PutMapping("/maximum-program")
    @ResponseStatus(code = HttpStatus.OK)
    public UserWithSessionDTO applyForMaximumProgram(@RequestBody DateDTO dateDTO, HttpSession session) {
        int userId = getUserId(session);
        return userService.applyForMaximumProgram(userId, dateDTO);
    }
}
