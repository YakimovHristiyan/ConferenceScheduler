package com.example.conferencescheduler.controllers;

import com.example.conferencescheduler.model.dtos.ExceptionDTO;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.ForbiddenException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
public abstract class AbstractController {

    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String REMOTE_ADDRESS = "remote_address";

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
}