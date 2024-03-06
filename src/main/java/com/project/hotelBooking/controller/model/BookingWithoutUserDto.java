package com.project.hotelBooking.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingWithoutUserDto {
    private Long id;
    private Long roomId;
    private LocalDate start_date;
    private LocalDate end_date;
}
