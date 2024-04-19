package com.project.hotelBooking.service.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class BookingServ {
    Long id;
    Long userId;
    Long roomId;
    LocalDate start_date;
    LocalDate end_date;
}
