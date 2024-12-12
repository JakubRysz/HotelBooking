package com.project.hotelBooking.controller.model.room;

import com.project.hotelBooking.controller.model.booking.BookingWithoutUserDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RoomWithBookingsWithoutUsersDto {
    Long id;
    int roomNumber;
    int numberOfPersons;
    int standard;
    Long hotelId;
    List<BookingWithoutUserDto> bookings;
}
