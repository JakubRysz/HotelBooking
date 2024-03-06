package com.project.hotelBooking.controller.model;

import com.project.hotelBooking.controller.model.BookingDto;
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
