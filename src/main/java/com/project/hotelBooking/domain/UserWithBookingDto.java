package com.project.hotelBooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserWithBookingDto {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String username;
    private String password;
    private String role;
    private String email;
    private List<Booking> bookings;
}
