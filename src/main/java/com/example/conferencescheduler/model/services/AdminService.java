package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.hallDTOs.CreateHallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallDTO;
import com.example.conferencescheduler.model.entities.Hall;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.entities.UserRole;
import org.springframework.stereotype.Service;

@Service
public class AdminService extends MasterService {

    public String changeUserRoleToConferenceOwner(int uid) {
        User user = getUserById(uid);
        UserRole userRole = userRoleRepository.findByRoleId(CONFERENCE_OWNER_ROLE);
        user.setUserRole(userRole);
        userRepository.save(user);
        return "User with id : " + user.getUserId() + " role changed to conference owner";
    }

    public HallDTO createHall(CreateHallDTO hall, int userId) {
        getUserById(userId);
        Hall hallToSave = modelMapper.map(hall, Hall.class);
        hallRepository.save(hallToSave);
        return modelMapper.map(hallToSave, HallDTO.class);
    }

}
