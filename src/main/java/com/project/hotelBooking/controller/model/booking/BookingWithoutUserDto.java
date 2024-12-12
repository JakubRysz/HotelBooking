package com.project.hotelBooking.controller.model.booking;

import lombok.*;

import java.time.LocalDate;

@Value
@Builder
public class BookingWithoutUserDto {
    Long id;
    Long roomId;
    LocalDate startDate;
    LocalDate endDate;
}
