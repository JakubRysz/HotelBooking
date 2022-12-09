package com.project.hotelBooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomWithBookingsDto {
    private Long id;
    private int roomNumber;
    private int numberOfPersons;
    private int standard;
    private Long hotelId;
    private List<BookingDto> bookings;
}
