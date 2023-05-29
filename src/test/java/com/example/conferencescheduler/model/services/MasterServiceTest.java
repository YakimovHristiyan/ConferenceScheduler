package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.entities.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MasterServiceTest {

    @Mock
    MasterService masterService;

    @Test
    void isPassValid() {
    }

    @Test
    void isPhoneValid() {
    }

    @Test
    void isEmailValid() {
    }

    @Test
    void isUsernameValid() {
    }

    @Test
    void sendVerificationEmail() {
    }

    @Test
    void sendEmail() {
    }

    @Test
    void getFileExtension() {
    }

    @Test
    void generateUserObject() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setFirstName("Teodor");
        dto.setPassword("123");
        User user = new User();
        dto.setFirstName("Teodor");
        dto.setPassword("123");
        when(masterService.generateUserObject(dto,1)).thenReturn(user);
       User result = masterService.generateUserObject(dto,1);
       assertEquals(result.getFirstName(), dto.getFirstName());
       assertEquals(result.getPassword(), dto.getPassword());
    }

    @Test
    void dateConverter() {
    }

    @Test
    void checkHoursAreFree() {
    }

    @Test
    void validatePassword() {
    }

    @Test
    void validateEmail() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void getUserByEmail() {
    }

    @Test
    void getConferenceById() {
    }

    @Test
    void getHallById() {
    }

    @Test
    void getSessionById() {
    }

    @Test
    void getSpeakerById() {
    }

    @Test
    void getStatusById() {
    }
}