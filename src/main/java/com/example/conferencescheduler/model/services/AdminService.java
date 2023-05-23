package com.example.conferencescheduler.model.services;

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

}
