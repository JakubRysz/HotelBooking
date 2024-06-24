package com.project.hotelBooking.controller.model;

import lombok.*;

import java.time.LocalDate;

@Value
@Builder
public class BookingWithoutUserDto {
    Long id;
    Long roomId;
    LocalDate start_date;
    LocalDate end_date;
}
