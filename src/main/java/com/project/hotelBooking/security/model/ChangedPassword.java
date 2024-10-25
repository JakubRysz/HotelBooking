package com.project.hotelBooking.security.model;

import lombok.Getter;

@Getter
public class ChangedPassword {
    private String password;
    private String repeatPassword;
    private String hash;
}
