package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.userDTOs.EditUserDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserLoginDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserWithoutPassDTO;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.entities.UserRole;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import com.example.conferencescheduler.model.repositories.UserRoleRepository;
import lombok.Builder;
import org.apache.commons.validator.routines.EmailValidator;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                return modelMapper.map(user.get(), UserWithoutPassDTO.class);
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

}
