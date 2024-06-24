package com.project.hotelBooking.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RoomWithBookingsDto {
    Long id;
    int roomNumber;
    int numberOfPersons;
    int standard;
    Long hotelId;
    List<BookingDto> bookings;
}
