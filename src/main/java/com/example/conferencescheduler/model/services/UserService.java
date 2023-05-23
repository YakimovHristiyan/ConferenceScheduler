package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.hallDTOs.DateDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.*;
import com.example.conferencescheduler.model.entities.*;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.ForbiddenException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends MasterService {

    public UserWithoutPassDTO register(UserRegisterDTO dto) {
//        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        User user = generateUserObject(dto, USER_ROLE);
        userRepository.save(user);
        sendVerificationEmail(user.getEmail(), user.getUserId());
        return modelMapper.map(user, UserWithoutPassDTO.class);
    }


    public UserWithoutPassDTO userLogin(UserLoginDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        if (!validateEmail(email) || !validatePassword(password)) {
            throw new BadRequestException("The fields are mandatory!");
        }
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User u = user.get();
            if (encoder.matches(password, u.getPassword())) {
                if (u.isVerified()) {
                    return modelMapper.map(user.get(), UserWithoutPassDTO.class);
                } else {
                    User userToSendMail = getUserByEmail(dto.getEmail());
                    sendVerificationEmail(dto.getEmail(), userToSendMail.getUserId());
                    throw new UnauthorizedException("Your account is not verified. " +
                            "An email has been sent to your email address to confirm your registration!");
                }
            } else {
                throw new UnauthorizedException("Wrong credentials!");
            }
        } else {
            throw new UnauthorizedException("Wrong credentials!");
        }
    }

    public EditUserDTO editAccount(EditUserDTO newUser, int id) {
        if (!isEmailValid(newUser.getEmail())) {
            throw new BadRequestException("Invalid email!");
        }
        if (!isPhoneValid(newUser.getPhoneNumber())) {
            throw new BadRequestException("Invalid phone number!");
        }
        User editedUser = getUserById(id);
        editedUser.setFirstName(newUser.getFirstName());
        editedUser.setLastName(newUser.getLastName());
        editedUser.setPhone(newUser.getPhoneNumber());
        editedUser.setEmail(newUser.getEmail());
        userRepository.save(editedUser);
        return modelMapper.map(editedUser, EditUserDTO.class);
    }

    public String verifyEmail(int uid) {
        User user = getUserById(uid);
        if (user.isVerified()) {
            throw new BadRequestException("User is already verified.");
        }
        user.setVerified(true);
        userRepository.save(user);
        return "Your account is now verified.";
    }

    @Transactional
    public UserWithSessionDTO assertAttendance(int userId, AttendanceDTO attendanceDTO) {
        //1.Get User by id
        User user = getUserById(userId);
        //2.Get Conference by id
        Conference conference = getConferenceById(attendanceDTO.getConferenceId());
        //3.Get session by id
        Session session = sessionRepository.findBySessionId(attendanceDTO.getSessionId())
                .orElseThrow(() -> new NotFoundException("Session not found."));
        //4.Check if already this user is going on this conference
        if (user.getSessions().contains(session)) {
            throw new BadRequestException("You are already guest for this session.");
        }

            /*
           5.Can not mark colliding sessions in more than one hall
           - iterate over all session in the list and check if
           there have another session in this time in another room
            */
        //6. Add session in the list
        LocalDateTime wantedSessionStartDate = session.getStartDate();
        LocalDateTime wantedSessionEndDate = session.getEndDate();
        boolean hasColliding = false;
        if (user.getSessions().isEmpty()) {
            saveSessionIfFree(session, user);
            return modelMapper.map(user, UserWithSessionDTO.class);
        }
        for (Session s : user.getSessions()) {
            LocalDateTime currentSessionStartDate = s.getStartDate();
            LocalDateTime currentSessionEndDate = s.getEndDate();
            Hall hall = s.getHall();
            if (session.getHall().getHallId() != hall.getHallId()) {
                if (!wantedSessionStartDate.isAfter(currentSessionEndDate)
                        && !wantedSessionEndDate.isBefore(currentSessionStartDate)) {
                    hasColliding = true;
                    break;
                }
            }
        }
        System.out.println(hasColliding);
        if (hasColliding) {
            throw new BadRequestException("Current session is colliding with other session in your list.");
        }
        saveSessionIfFree(session, user);

        return modelMapper.map(user, UserWithSessionDTO.class);
    }

    private void saveSessionIfFree(Session session, User user) {
        if (session.getGuests().size() == session.getHall().getCapacity()) {
            throw new BadRequestException("There is no more free seat in the hall.");
        } else {
            user.getSessions().add(session);
            session.getGuests().add(user);
            userRepository.save(user);
            sessionRepository.save(session);
        }
    }

    public UserWithSessionDTO applyForMaximumProgram(int userId, DateDTO dateDTO) {
        if (getUserById(userId).getUserRole().getRoleId() != USER_ROLE) {
            throw new ForbiddenException("You can not apply for sessions!");
        }
        List<Session> sessionsByDate = sessionRepository.getSessionByDateOrderByStartDate(dateDTO.getDate());
        User user = assertAttendanceToAvailableSessions(getUserById(userId), dateDTO, sessionsByDate);
        userRepository.save(user);
        return modelMapper.map(user, UserWithSessionDTO.class);
    }

    private User assertAttendanceToAvailableSessions(User user, DateDTO dateDTO, List<Session> availableSessions) {
        // Deleting already assigned session in order to use max program
        List<Session> assignedSessionsForTheDay = user.getSessions().stream().
                filter(e -> e.getStartDate().toLocalDate()
                        .isEqual(dateDTO.getDate()))
                .toList();
        if (!assignedSessionsForTheDay.isEmpty()) {
            assignedSessionsForTheDay.forEach(e -> user.getSessions().remove(e));
        }

        Session previousAssignedSession = null;
        for (Session availableSession : availableSessions) {
            if (previousAssignedSession == null) {
                user.getSessions().add(availableSession);
                previousAssignedSession = availableSession;
                continue;
            } else if (!availableSession.getStartDate().isAfter(previousAssignedSession.getEndDate())
                    && !availableSession.getEndDate().isBefore(previousAssignedSession.getStartDate())) {
                continue;

            }
            user.getSessions().add(availableSession);
            previousAssignedSession = availableSession;
        }
        return user;
    }

}
