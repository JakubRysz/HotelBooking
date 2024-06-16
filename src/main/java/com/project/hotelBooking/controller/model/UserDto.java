package com.project.hotelBooking.controller.model;

import lombok.*;

import java.time.LocalDate;

@Value
@Builder
public class UserDto {
    Long id;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String username;
    String password;
    @With
    String role;
    String email;
}