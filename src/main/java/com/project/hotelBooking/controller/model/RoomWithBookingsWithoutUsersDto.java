package com.project.hotelBooking.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomWithBookingsWithoutUsersDto {
    private Long id;
    private int roomNumber;
    private int numberOfPersons;
    private int standard;
    private Long hotelId;
    private List<BookingWithoutUserDto> bookings;
}
