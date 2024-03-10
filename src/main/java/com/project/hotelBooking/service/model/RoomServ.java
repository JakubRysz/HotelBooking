package com.project.hotelBooking.service.model;

import lombok.*;

import java.util.List;

@Value
@Builder
public class RoomServ {
    Long id;
    int roomNumber;
    int numberOfPersons;
    int standard;
    Long hotelId;
    List<BookingServ> bookings;

}
