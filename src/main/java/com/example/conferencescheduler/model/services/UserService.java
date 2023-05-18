package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserWithoutPassDTO;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserService extends MasterService {

    @Autowired
    private JavaMailSender emailSender;

    private static final int SPEAKER_ROLE_ID = 1;

    @Transactional
    public UserWithoutPassDTO register(UserRegisterDTO dto) {
//        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        validateUserInformation(dto);
        User user = modelMapper.map(dto, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRegisterAt(LocalDateTime.now());
        userRepository.save(user);
        sendVerificationEmail(user.getEmail());
        return modelMapper.map(user, UserWithoutPassDTO.class);
    }

    private void sendVerificationEmail(String email) {
        new Thread(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("codexio.scheduler@gmail.com");
            message.setTo(email);
            message.setSubject("Your Conference Scheduler Account - Verify Your Email Address");
            message.setText("Please, follow the link bellow in order to verify your email address:\n" +
                    "http://localhost:7000/users/email-verification");//TODO decide what port to use
            emailSender.send(message);
        }).start();
    }

    public String verifyEmail(int uid) {
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
