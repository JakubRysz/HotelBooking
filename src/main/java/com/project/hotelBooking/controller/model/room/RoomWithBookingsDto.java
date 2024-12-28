package com.project.hotelBooking.controller.model.room;

import com.project.hotelBooking.controller.model.booking.BookingDto;
import lombok.Builder;
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
