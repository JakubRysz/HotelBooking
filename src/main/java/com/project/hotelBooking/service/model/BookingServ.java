package com.project.hotelBooking.service.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDate;

@Value
@Builder
public class BookingServ {
    Long id;
    @With
    Long userId;
    Long roomId;
    LocalDate startDate;
    LocalDate endDate;
}
