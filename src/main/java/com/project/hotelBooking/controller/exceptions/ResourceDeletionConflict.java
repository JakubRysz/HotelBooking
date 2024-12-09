package com.project.hotelBooking.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceDeletionConflict extends RuntimeException {

    public ResourceDeletionConflict(String message) {
        super(message);
    }
}

