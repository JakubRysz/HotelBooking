package com.project.hotelBooking.controller.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ElementAlreadyExistException extends RuntimeException {

        public ElementAlreadyExistException(String message) {
            super(message);
        }
}

