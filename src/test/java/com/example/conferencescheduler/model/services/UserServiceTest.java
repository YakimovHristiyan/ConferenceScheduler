package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.MasterTest;
import com.example.conferencescheduler.model.dtos.hallDTOs.DateDTO;
import com.example.conferencescheduler.model.dtos.sessionDTOs.AddedSessionDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.*;
import com.example.conferencescheduler.model.entities.*;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.conferencescheduler.model.services.MasterService.USER_ROLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest extends MasterTest {

    private final static int USER_ID = 1;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerShouldReturnUserWithoutPassDTO() {
        // Arrange
        String password = "Hybernate123@";
        String email = "tp@abv.bg";
        String name = "Teodor";

        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setPassword(password);
        registerDTO.setConfirmPassword(password);
        registerDTO.setEmail(email);
        registerDTO.setPhone("0872343451");
        registerDTO.setFirstName(name);

        UserWithoutPassDTO expectedDTO = new UserWithoutPassDTO();
        expectedDTO.setId(USER_ID);
        expectedDTO.setFirstName(name);

        when(modelMapper.map(any(User.class), any(Class.class))).thenReturn(expectedDTO);

        // Act
        UserWithoutPassDTO resultDTO = userService.register(registerDTO);

        // Assert
        assertEquals(expectedDTO.getId(), resultDTO.getId());
        assertEquals(expectedDTO.getFirstName(), resultDTO.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void userLoginWithValidCredentialsAndVerified() {
        // Arrange
        String password = "Hybernate123@";
        String email = "tp@abv.bg";
        String name = "Teodor";

        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(name);
        user.setVerified(true);

        UserWithoutPassDTO expectedDTO = new UserWithoutPassDTO();
        expectedDTO.setId(USER_ID);
        expectedDTO.setFirstName(name);

        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserWithoutPassDTO.class))).thenReturn(expectedDTO);
        when(encoder.matches(user.getPassword(), loginDTO.getPassword())).thenReturn(true);

        // Act
        UserWithoutPassDTO resultDTO = userService.userLogin(loginDTO);

        // Assert
        assertEquals(expectedDTO.getId(), resultDTO.getId());
        assertEquals(expectedDTO.getFirstName(), resultDTO.getFirstName());
        verify(userRepository, times(1)).findByEmail(eq(email));
    }

    @Test
    void editAccountAndReturnUser() {
        String newFirstName = "John";
        String newLastName = "Doe";
        String newEmail = "john.doe@example.com";
        String newPhoneNumber = "0234567890";

        EditUserDTO newUserDTO = new EditUserDTO();
        newUserDTO.setFirstName(newFirstName);
        newUserDTO.setLastName(newLastName);
        newUserDTO.setEmail(newEmail);
        newUserDTO.setPhoneNumber(newPhoneNumber);

        User existingUser = new User();
        existingUser.setUserId(USER_ID);
        existingUser.setFirstName("Old");
        existingUser.setLastName("User");
        existingUser.setEmail("old.user@example.com");
        existingUser.setPhone("9876543210");

        EditUserDTO expectedEditedUserDTO = new EditUserDTO();
        expectedEditedUserDTO.setFirstName(newFirstName);
        expectedEditedUserDTO.setLastName(newLastName);
        expectedEditedUserDTO.setEmail(newEmail);
        expectedEditedUserDTO.setPhoneNumber(newPhoneNumber);

        when(modelMapper.map(any(User.class), eq(EditUserDTO.class))).thenReturn(expectedEditedUserDTO);
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(existingUser));

        // Act
        EditUserDTO result = userService.editAccount(newUserDTO, USER_ID);

        // Assert
        assertEquals(expectedEditedUserDTO.getFirstName(), result.getFirstName());
        assertEquals(expectedEditedUserDTO.getLastName(), result.getLastName());
        assertEquals(expectedEditedUserDTO.getEmail(), result.getEmail());
        assertEquals(expectedEditedUserDTO.getPhoneNumber(), result.getPhoneNumber());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void verifyEmailAndCheckMessage() {
        String wantedMsg = "Your account is now verified.";
        User existingUser = new User();
        existingUser.setUserId(USER_ID);
        existingUser.setFirstName("Old");
        existingUser.setLastName("User");
        existingUser.setEmail("old.user@example.com");
        existingUser.setPhone("9876543210");


        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
        // Act
        String msg = userService.verifyEmail(USER_ID);
        // Assert
        assertEquals(wantedMsg, msg);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void verifyEmailAndThrowWhenIsAlreadyVerified() {
        String wantedMsg = "Your account is now verified.";
        User existingUser = new User();
        existingUser.setUserId(USER_ID);
        existingUser.setFirstName("Old");
        existingUser.setLastName("User");
        existingUser.setEmail("old.user@example.com");
        existingUser.setPhone("9876543210");
        existingUser.setVerified(true);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
        // Act
        Exception exception = assertThrows(BadRequestException.class, () -> {
            userService.verifyEmail(USER_ID);
        });
        String expectedMessage = "User is already verified.";
        String actualMessage = exception.getMessage();
        // Assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAssertAttendanceWhenUserAlreadyGuestForSessionThrowsBadRequestException() {
        // Arrange
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setConferenceId(1);
        attendanceDTO.setSessionId(1);

        User user = new User();
        user.setUserId(USER_ID);
        user.setSessions(new ArrayList<>());

        Session session = new Session();
        session.setSessionId(attendanceDTO.getSessionId());

        Conference conference = new Conference();
        conference.setConferenceId(1);
        user.getSessions().add(session);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(conferenceRepository.findById(attendanceDTO.getConferenceId())).thenReturn(Optional.of(conference));
        when(sessionRepository.findBySessionId(attendanceDTO.getSessionId())).thenReturn(Optional.of(session));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.assertAttendance(USER_ID, attendanceDTO));

    }

    @Test
    public void testAssertAttendanceWhenSessionHallIsFullThrowsBadRequestException() {
        // Arrange
        int userId = 1;
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setConferenceId(1);
        attendanceDTO.setSessionId(1);

        User user = new User();
        user.setUserId(userId);
        user.setSessions(new ArrayList<>());

        Conference conference = new Conference();
        conference.setConferenceId(1);

        Session session = new Session();
        session.setSessionId(attendanceDTO.getSessionId());
        session.setGuests(new ArrayList<>());

        Hall hall = new Hall();
        hall.setHallId(1);
        hall.setCapacity(5);
        session.setHall(hall);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.findBySessionId(attendanceDTO.getSessionId())).thenReturn(Optional.of(session));
        when(conferenceRepository.findById(attendanceDTO.getConferenceId())).thenReturn(Optional.of(conference));
        when(sessionRepository.save(session)).thenReturn(session);

        // Add 5 guests to fill the hall capacity
        for (int i = 0; i < 5; i++) {
            User guest = new User();
            guest.setUserId(i + 10);
            session.getGuests().add(guest);
        }

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.assertAttendance(userId, attendanceDTO));
    }

    @Test
    public void testAssertAttendanceWhenUserHasNoSessionsSaveSession() {
        // Arrange
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setConferenceId(1);
        attendanceDTO.setSessionId(1);

        User user = new User();
        user.setUserId(USER_ID);
        user.setSessions(new ArrayList<>());

        Conference conference = new Conference();
        conference.setConferenceId(1);

        Session session = new Session();
        session.setSessionId(attendanceDTO.getSessionId());
        session.setGuests(new ArrayList<>());

        Hall hall = new Hall();
        hall.setHallId(1);
        hall.setCapacity(5);
        session.setHall(hall);

        UserWithSessionDTO dto = new UserWithSessionDTO();
        dto.setUserId(user.getUserId());
        dto.setSessions(new ArrayList<>());
        dto.getSessions().add(modelMapper.map(session, AddedSessionDTO.class));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(sessionRepository.findBySessionId(attendanceDTO.getSessionId())).thenReturn(Optional.of(session));
        when(conferenceRepository.findById(attendanceDTO.getConferenceId())).thenReturn(Optional.of(conference));
        when(sessionRepository.save(session)).thenReturn(session);
        when(modelMapper.map(any(User.class), eq(UserWithSessionDTO.class))).thenReturn(dto);

        // Act
        UserWithSessionDTO result = userService.assertAttendance(USER_ID, attendanceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(1, result.getSessions().size());
        assertTrue(result.getSessions().contains(dto.getSessions().get(0)));
        verify(userRepository, times(1)).save(user);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    public void testAssertAttendanceNotCollideUserSessionsAndSaveSession() {
        // Arrange
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setConferenceId(1);
        attendanceDTO.setSessionId(1);

        User user = new User();
        user.setUserId(USER_ID);
        user.setSessions(new ArrayList<>());

        Conference conference = new Conference();
        conference.setConferenceId(1);

        Session session = new Session();
        session.setSessionId(attendanceDTO.getSessionId());
        session.setStartDate(LocalDateTime.now().plusMinutes(1));
        session.setEndDate(LocalDateTime.now().plusMinutes(10));
        session.setGuests(new ArrayList<>());
        Session userSession = new Session();
        session.setSessionId(attendanceDTO.getSessionId()+1);
        session.setStartDate(LocalDateTime.now().plusMinutes(20));
        session.setEndDate(LocalDateTime.now().plusMinutes(30));

        Hall hall = new Hall();
        hall.setHallId(1);
        hall.setCapacity(5);
        session.setHall(hall);

        UserWithSessionDTO dto = new UserWithSessionDTO();
        dto.setUserId(user.getUserId());
        dto.setSessions(new ArrayList<>());
        dto.getSessions().add(modelMapper.map(session, AddedSessionDTO.class));
        dto.getSessions().add(modelMapper.map(userSession, AddedSessionDTO.class));


        userSession.setGuests(new ArrayList<>());
        userSession.setSessionId(2);
        userSession.setHall(hall);
        user.getSessions().add(userSession);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(sessionRepository.findBySessionId(attendanceDTO.getSessionId())).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);
        when(conferenceRepository.findById(attendanceDTO.getConferenceId())).thenReturn(Optional.of(conference));
        when(modelMapper.map(any(User.class), eq(UserWithSessionDTO.class))).thenReturn(dto);

        // Act
        UserWithSessionDTO result = userService.assertAttendance(USER_ID, attendanceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(2, result.getSessions().size());
        assertTrue(result.getSessions().contains(dto.getSessions().get(0)));
        verify(userRepository, times(1)).save(user);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testApplyForMaximumProgramWhenSuccessful() {
        // Arrange
        LocalDateTime time = LocalDateTime.now();
        DateDTO dateDTO = new DateDTO();
        dateDTO.setDate(LocalDate.now());

        Hall hall = new Hall();
        hall.setHallId(1);
        hall.setCapacity(5);



        List<Session> sessionsByDate = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
        Session session = new Session();
        session.setStartDate(time.plusMinutes(i*10));
        session.setEndDate(time.plusMinutes(i*20));
        session.setGuests(new ArrayList<>());
        session.setHall(hall);
        sessionsByDate.add(session);
        }
        User user = new User();
        user.setUserRole(new UserRole());
        user.getUserRole().setRoleId(USER_ROLE);
        user.setSessions(new ArrayList<>());

        UserWithSessionDTO dto = new UserWithSessionDTO();
        dto.setUserId(user.getUserId());
        dto.setSessions(new ArrayList<>());


        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(null);
        when(sessionRepository.getSessionByDateOrderByStartDate(LocalDate.now())).thenReturn(sessionsByDate);
        when(modelMapper.map(any(User.class), eq(UserWithSessionDTO.class))).thenReturn(dto);

        // Act
        UserWithSessionDTO result = userService.applyForMaximumProgram(USER_ID, dateDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);
        verify(sessionRepository, times(1)).getSessionByDateOrderByStartDate(dateDTO.getDate());
        assertEquals(3, result.getSessions().size());
    }
}