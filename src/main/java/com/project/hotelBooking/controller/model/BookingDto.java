package com.project.hotelBooking.controller.model;

import lombok.*;

import java.time.LocalDate;
@Value
@Builder
public class BookingDto {
    Long id;
    Long userId;
    Long roomId;
    LocalDate start_date;
    LocalDate end_date;
}
