package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.ExceptionDTO;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.ForbiddenException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
public abstract class AbstractController {

    public static final String LOGGED = "LOGGED";
    public static final String USER_ID = "USER_ID";
    public static final String REMOTE_IP = "REMOTE_IP";

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ExceptionDTO badRequestHandler(Exception exception) {
        return buildExceptionDtoInfo(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ExceptionDTO notFoundHandler(Exception exception) {
        return buildExceptionDtoInfo(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ExceptionDTO unauthorizedHandler(Exception exception) {
        return buildExceptionDtoInfo(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    private ExceptionDTO forbiddenHandler(Exception exception) {
        return buildExceptionDtoInfo(exception, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    private ExceptionDTO otherExceptionsHandler(Exception exception) {
        exception.printStackTrace();
        return buildExceptionDtoInfo(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ExceptionDTO buildExceptionDtoInfo(Exception exception, HttpStatus status){
        ExceptionDTO exceptionDTO = new ExceptionDTO();
        exceptionDTO.setDateTime(LocalDateTime.now());
        exceptionDTO.setMsg(exception.getMessage());
        exceptionDTO.setStatus(status.value());
        return exceptionDTO;
    }

    protected int getUserId(HttpSession session) {
        if (session.getAttribute(USER_ID) == null) {
            throw new NotFoundException("User's ID is not in session."); // This should never happen.
        }
        return (int) session.getAttribute(USER_ID);
    }

    public void logUser(HttpServletRequest request, int id){
        HttpSession session = request.getSession();
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, id);
        session.setAttribute(REMOTE_IP, request.getRemoteAddr());
        session.setMaxInactiveInterval(30*60);
    }

    public int getLoggedUserId(HttpServletRequest request){
        HttpSession session = request.getSession();
        String ip = request.getRemoteAddr();
        System.out.println(session.getAttribute(LOGGED));
        System.out.println(session.getAttribute(USER_ID));
        if (session.isNew() ||
                session.getAttribute(LOGGED) == null ||
                (!(boolean) session.getAttribute(LOGGED)) ||
                !session.getAttribute(REMOTE_IP).equals(ip)){
            throw new UnauthorizedException("You are not logged!");
        }
        return (int) session.getAttribute(USER_ID);
    }
}
