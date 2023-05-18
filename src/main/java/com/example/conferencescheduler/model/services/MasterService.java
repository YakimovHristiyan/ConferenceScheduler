package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.repositories.*;
import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public abstract class MasterService {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected SpeakerRepository speakerRepository;
    @Autowired
    protected SessionRepository sessionRepository;
    @Autowired
    protected HallRepository hallRepository;
    @Autowired
    protected ConferenceRepository conferenceRepository;
    @Autowired
    protected PasswordEncoder encoder;
    @Autowired
    protected ModelMapper modelMapper;

    protected void validateUserInformation(UserRegisterDTO dto) {
        if (userRepository.findByPhone(dto.getPhone()).isPresent()){
            throw new BadRequestException("The phone number exist!");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BadRequestException("This email is already registered!");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords mismatch!");
        }
        if (!isEmailValid(dto.getEmail())) {
            throw new BadRequestException("Invalid email!");
        }
        if (!isPassValid(dto.getPassword())) {
            throw new BadRequestException("Invalid password!");
        }
        if (!isPhoneValid(dto.getPhone())) {
            throw new BadRequestException("Invalid phone number!");
        }
    }

    protected static boolean isPassValid(String password) {
        /*
        Must have at least one numeric character
        Must have at least one lowercase character
        Must have at least one uppercase character
        Must have at least one special symbol among @#$%
        Password length should be between 8 and 20
         */
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        boolean hasMatch = m.matches();
        if (hasMatch) {
            return true;
        }
        return false;
    }

    protected static boolean isPhoneValid(String phoneNumber) {
        Pattern p = Pattern.compile("0[0-9]{9}");
        Matcher m = p.matcher(phoneNumber);
        boolean hasMatch = m.matches();
        if (hasMatch) {
            return true;
        }
        return false;
    }

    protected static boolean isEmailValid(String email) {
        return EmailValidator.getInstance(true).isValid(email);
    }

    protected static boolean isUsernameValid(String username){
        Pattern p = Pattern.compile("^[a-zA-Z_]([a-zA-Z0-9_]){2,16}");
        Matcher m = p.matcher(username);
        boolean hasMatch = m.matches();
        if (hasMatch){
            return true;
        }
        return false;
    }

    protected static String getFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}
