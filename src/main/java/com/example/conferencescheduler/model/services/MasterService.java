package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.entities.*;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.repositories.*;
import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public abstract class MasterService {
    protected final static int USER_ROLE = 1;
    protected final static int SPEAKER_ROLE = 2;
    protected final static int CONFERENCE_OWNER_ROLE = 3;
    protected static final String DEF_PROFILE_IMAGE_URI = "uploads" + File.separator + "def_profile_image.png"; //TODO add the folder

    @Autowired
    private JavaMailSender emailSender;
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
    @Autowired
    protected UserRoleRepository userRoleRepository;

    private void validateUserInformation(UserRegisterDTO dto) {
        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
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

    protected static boolean isUsernameValid(String username) {
        Pattern p = Pattern.compile("^[a-zA-Z_]([a-zA-Z0-9_]){2,16}");
        Matcher m = p.matcher(username);
        boolean hasMatch = m.matches();
        if (hasMatch) {
            return true;
        }
        return false;
    }

    protected void sendVerificationEmail(String email, int uid) {
        new Thread(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("codexio.scheduler@gmail.com");
            message.setTo(email);
            message.setSubject("Your Conference Scheduler Account - Verify Your Email Address");
            message.setText("Please, follow the link bellow in order to verify your email address:\n" +
                    "http://localhost:8080/users/email-verification/" + uid);
            emailSender.send(message);
        }).start();
    }

    protected void sendEmail(String senderMail, String email, String subject, String text) {
        new Thread(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderMail);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        }).start();
    }

    protected static String getFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    protected User generateUserObject(UserRegisterDTO dto, int roleId) {
        validateUserInformation(dto);
        UserRole userRole = userRoleRepository.findByRoleId(roleId);
        System.out.println(dto.getRoleId());
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .userRole(userRole)
                .password(encoder.encode(dto.getPassword()))
                .registerAt(LocalDateTime.now())
                .build();
    }

    protected long dateConverter(LocalDateTime dateTime) {
        ZonedDateTime zdt = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    protected boolean checkHoursAreFree(Session assigned, Session possible){
        long startTime = dateConverter(possible.getStartDate());
        long endTime = dateConverter(possible.getEndDate());
        long startTimeOfExistingSession = dateConverter(assigned.getStartDate());
        long endTimeOfExistingSession = dateConverter(assigned.getEndDate());

        if (startTime <= startTimeOfExistingSession && endTime >= endTimeOfExistingSession) {
            return false;
        }
        if ((startTime >= startTimeOfExistingSession && startTime < endTimeOfExistingSession)
                || (endTime > startTimeOfExistingSession && endTime <= endTimeOfExistingSession)) {
            return false;
        }
        return true;
    }

    protected boolean validatePassword(String password) {
        return password != null && !password.isBlank();
    }

    protected boolean validateEmail(String email) {
        return email != null && !email.isBlank();
    }

    protected User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found!"));
    }

    protected User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found!"));
    }

    protected Conference getConferenceById(int conferenceId) {
        return conferenceRepository.findById(conferenceId).orElseThrow(() -> new NotFoundException("Conference not found!"));
    }

    protected Hall getHallById(int hallId) {
        return hallRepository.findById(hallId).orElseThrow(() -> new NotFoundException("Hall does not exist!"));
    }

    protected Session getSessionById(int sessionId) {
        return sessionRepository.findBySessionId(sessionId).orElseThrow(() -> new NotFoundException("Session not found."));
    }
}
