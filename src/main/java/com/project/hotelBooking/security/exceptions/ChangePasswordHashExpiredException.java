package com.project.hotelBooking.security.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.GONE)
public class ChangePasswordHashExpiredException extends RuntimeException {
    public ChangePasswordHashExpiredException(String message) {
        super(message);
    }
}
