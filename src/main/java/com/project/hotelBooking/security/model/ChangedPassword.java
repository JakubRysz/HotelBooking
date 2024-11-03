package com.project.hotelBooking.security.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangedPassword {
    private String password;
    private String repeatPassword;
    private String hash;
}
