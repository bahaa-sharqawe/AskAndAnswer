package com.orchidatech.askandanswer.View.Utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bahaa on 12/11/2015.
 */
public class Validator {
    private static final int MIN_PASSWORD_LENGTH = 8;
    public static Validator instance;
    private Pattern pattern;
    private Matcher matcher;
//    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private static final String USERNAME_PATTERN = "^[\\p{L} .-_]+$";
    private static final String EMAIL_PATTERN = "^[A-Za-z][A-Za-z0-9]*([._-]?[A-Za-z0-9]+)@[A-Za-z].[A-Za-z]{0,3}?.[A-Za-z]{0,2}$";
/*
    ^                   # Start of the line
    [a-z0-9_-]	     # Match characters and symbols in the list,a-z, 0-9, underscore, hyphen
    {3,15}  # Length at least 3 characters and maximum length of 15
    $                  # End of the line*/

    private Validator(){}
    public static Validator getInstance(){
        if(instance == null)
            instance = new Validator();
        return instance;
    }

    public  boolean isValidUserName(String username){
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);
        return matcher.matches();
    }
    public  boolean isWebUrl(String url){
        pattern = Patterns.WEB_URL;
        matcher = pattern.matcher(url);
        return matcher.matches();
    }
    public  boolean isValidEmail(String email){
        pattern = Patterns.EMAIL_ADDRESS;
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean isPasswordsMatched(String password, String confirmPassword){
        if(!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && password.equals(confirmPassword))
            return true;
        return false;
    }

    public boolean isValidPassword(String password) {
        if(password.length() < MIN_PASSWORD_LENGTH){
            return false;
        }else if(!containsLettersAndDigits(password)){
            return false;
        }
        return true;
    }

    private boolean containsLettersAndDigits(String password) {
        int numDigits = 0;
        int numLetters = 0;
        for(int i = 0; i < password.length(); i++){
            if(Character.isDigit(password.charAt(i)))
                numDigits++;
            else
                numLetters++;
        }
        return (numDigits == 0 || numLetters == 0)?false:true;
    }

    public String getSafeText(String message) {
            return  message.replaceAll("\\<[^>]*>","");

    }
}
