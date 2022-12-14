package com.project.hotelBooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private Long userId;
    private Long roomId;
    private LocalDate start_date;
    private LocalDate end_date;
}
