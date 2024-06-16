package com.project.hotelBooking.controller.model;

import com.project.hotelBooking.repository.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class UserWithBookingDto {
    Long id;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String username;
    String password;
    String role;
    String email;
    List<Booking> bookings;
}
