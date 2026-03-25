package com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user;

public class UserNotFoundException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE = "User not found";

    public UserNotFoundException() {
        super(UserNotFoundException.EXCEPTION_MESSAGE);
    }
}
