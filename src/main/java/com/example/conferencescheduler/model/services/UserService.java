package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.userDTOs.*;
import com.example.conferencescheduler.model.entities.*;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import com.example.conferencescheduler.model.repositories.UserRoleRepository;
import lombok.Builder;
import lombok.ToString;
import org.apache.commons.validator.routines.EmailValidator;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

@Service
public class UserService extends MasterService {

    private static final int SPEAKER_ROLE_ID = 1;


    public UserWithoutPassDTO register(UserRegisterDTO dto) {
//        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        validateUserInformation(dto);
        UserRole userRole = userRoleRepository.findByRoleId(dto.getRoleId());
        System.out.println(dto.getRoleId());
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .userRole(userRole)
                .password(encoder.encode(dto.getPassword()))
                .registerAt(LocalDateTime.now())
                .build();
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
                    throw new UnauthorizedException("Your account is not verified");
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

    public String verifyEmail(int uid) { //TODO test if this is working correctly
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("User not found."));
        if (user.isVerified()) {
            throw new BadRequestException("User is already verified.");
        }
        user.setVerified(true);
        userRepository.save(user);
        return "Your account is now verified.";
    }

    public UserWithSessionDTO assertAttendance(int userId, AttendanceDTO attendanceDTO) {
        //1.Get User by id
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        //2.Get Conference by id
        Conference conference = conferenceRepository.findById(attendanceDTO.getConferenceId())
                .orElseThrow(() -> new NotFoundException("Conference not found."));
        //3.Get session by id
        Session session = sessionRepository.findBySessionId(attendanceDTO.getSessionId())
                .orElseThrow(() -> new NotFoundException("Session not found."));
        if (session == null) {
            throw new BadRequestException("Session not found.");
        }
        //4.Check if already this user is going on this conference
        if (user.getSessions().contains(session)) {
            throw new BadRequestException("You are already guest for this session.");
        } else {
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
                if (session.getGuests().size() == session.getHall().getCapacity()) {
                    throw new BadRequestException("There is no more free seat in the hall.");
                } else {
                    user.getSessions().add(session);
                    session.getGuests().add(user);
                    userRepository.save(user);
                    sessionRepository.save(session);
                    return modelMapper.map(user, UserWithSessionDTO.class);
                }
            } else {
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

            }
            System.out.println(hasColliding);
            if (hasColliding) {
                throw new BadRequestException("Current session is colliding with other session in your list.");
            } else {
                if (session.getGuests().size() == session.getHall().getCapacity()) {
                    throw new BadRequestException("There is no more free seat in the hall.");
                } else {
                    user.getSessions().add(session);
                    session.getGuests().add(user);
                    userRepository.save(user);
                    sessionRepository.save(session);
                }
            }
        }
        return modelMapper.map(user, UserWithSessionDTO.class);
    }
}
