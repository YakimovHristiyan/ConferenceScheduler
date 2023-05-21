package com.example.conferencescheduler.utility;

import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class SecurityInterceptor implements HandlerInterceptor {
    public static final String LOGGED = "logged";
    public static final String REMOTE_ADDRESS = "remote_address";

    @Override//TODO DA SE OPRAVI!!!!!!!!!!!!
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (uri.contains("users/registration") ||
                uri.contains("email-verification") ||
                uri.contains("auth") ||
                uri.contains("logout")) {
            return true;
        }
        HttpSession session = request.getSession();
        String ip = request.getRemoteAddr();
        if (session.getAttribute(LOGGED) == null ||
                !(boolean) session.getAttribute(LOGGED) ||
                session.getAttribute(REMOTE_ADDRESS) == null ||
                !session.getAttribute(REMOTE_ADDRESS).equals(ip)) {
            throw new UnauthorizedException("You should log in first.");
        }
        return true;
    }
}
