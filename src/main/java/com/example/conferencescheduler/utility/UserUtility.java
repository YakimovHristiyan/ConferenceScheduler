package com.example.conferencescheduler.utility;

import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UserUtility {

    private static final int USER_ROLE_ID = 1;
    private static final int CRAFTSMAN_ROLE_ID = 2;


    public static boolean isPassValid(String password) {
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

    public static boolean isPhoneValid(String phoneNumber) {
        Pattern p = Pattern.compile("0[0-9]{9}");
        Matcher m = p.matcher(phoneNumber);
        boolean hasMatch = m.matches();
        if (hasMatch) {
            return true;
        }
        return false;
    }


    public static boolean isEmailValid(String email) {
        return EmailValidator.getInstance(true).isValid(email);
    }

    public static boolean isUsernameValid(String username){
        Pattern p = Pattern.compile("^[a-zA-Z_]([a-zA-Z0-9_]){2,16}");
        Matcher m = p.matcher(username);
        boolean hasMatch = m.matches();
        if (hasMatch){
            return true;
        }
        return false;
    }

    public static String getFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

}
