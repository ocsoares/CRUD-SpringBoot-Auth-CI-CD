package com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE = "User with this email already exists";

    public UserAlreadyExistsException() {
        super(EXCEPTION_MESSAGE);
    }
}
