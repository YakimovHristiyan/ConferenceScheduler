package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.hallDTOs.DateDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.AddedSessionDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.*;
import com.example.conferencescheduler.model.entities.*;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.ForbiddenException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                    u.setLastLoginAt(LocalDateTime.now());
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
        User user = getUserById(userId);
        getConferenceById(attendanceDTO.getConferenceId());
        Session session = getSessionById(attendanceDTO.getSessionId());

        if (user.getSessions().contains(session)) {
            throw new BadRequestException("You are already guest for this session.");
        }
//
//           5.Can not mark colliding sessions in more than one hall
//           - iterate over all session in the list and check if
//           there have another session in this time in another room

        //6. Add session in the list
        boolean isFree = true;
        if (user.getSessions().isEmpty()) {
            saveSessionIfFree(session, user);
            return modelMapper.map(user, UserWithSessionDTO.class);
        }
        for (Session userSession : user.getSessions()) {
            Hall hall = userSession.getHall();
            if (session.getHall().getHallId() != hall.getHallId()) {
                isFree = checkHoursAreFree(userSession, session);
                if(!isFree){
                    break;
                }
            }
        }
        if (!isFree) {
            throw new BadRequestException("Current session is colliding with other session in your list.");
        }
        saveSessionIfFree(session, user);
        return modelMapper.map(user, UserWithSessionDTO.class);
    }

    private void saveSessionIfFree(Session session, User user) {
        if (session.getGuests().size() == session.getHall().getCapacity()) {
            throw new BadRequestException("There is no more free seat in the hall.");
        }
        user.getSessions().add(session);
        session.getGuests().add(user);
        userRepository.save(user);
        sessionRepository.save(session);
    }

    @Transactional
    public UserWithSessionDTO applyForMaximumProgram(int userId, DateDTO dateDTO) {
        List<Session> sessionsByDate = sessionRepository.getSessionByDateOrderByStartDate(dateDTO.getDate());
        List<Session> possibleSessions = new ArrayList<>();
        if (getUserById(userId).getUserRole().getRoleId() != USER_ROLE) {
            throw new ForbiddenException("You can not apply for sessions!");
        }
        if (sessionsByDate.isEmpty()) {
            throw new BadRequestException("There are no session for this date!");
        }
        User user = assertAttendanceToAvailableSessions(getUserById(userId), dateDTO, sessionsByDate, possibleSessions);
        userRepository.save(user);
        UserWithSessionDTO dto = modelMapper.map(user, UserWithSessionDTO.class);
        dto.setSessions(user.getSessions().stream().map(session -> modelMapper.map(session, AddedSessionDTO.class)).collect(Collectors.toList()));
        return dto;
    }

    private User assertAttendanceToAvailableSessions(User user, DateDTO dateDTO,
                                                     List<Session> availableSessions, List<Session> possibleSessions) {
        Session previousAssignedSession = null;
        for (Session availableSession : availableSessions) {
            if (previousAssignedSession == null) {
                saveSessionIfFree(availableSession, user, possibleSessions);
                previousAssignedSession = availableSession;
                continue;
            }
            boolean isHourFree = checkHoursAreFree(previousAssignedSession, availableSession);
            if(isHourFree){
            saveSessionIfFree(availableSession, user, possibleSessions);
            previousAssignedSession = availableSession;
            }
        }
        user.getSessions().addAll(possibleSessions);
        return user;
    }

    private void saveSessionIfFree(Session session, User user, List<Session> possibleSessions) {
        if (session.getGuests().size() == session.getHall().getCapacity()) {
            throw new BadRequestException("There is no more free seat in the hall.");
        }
        if (user.getSessions().contains(session)) {
            return;
        }
        session.getGuests().add(user);
        sessionRepository.save(session);
        possibleSessions.add(session);
    }
}
