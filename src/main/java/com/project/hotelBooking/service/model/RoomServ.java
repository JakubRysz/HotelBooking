package com.project.hotelBooking.service.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;

@Value
@Builder
public class RoomServ {
    Long id;
    int roomNumber;
    int numberOfPersons;
    int standard;
    Long hotelId;
    @With
    List<BookingServ> bookings;
}
