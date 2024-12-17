package com.project.hotelBooking.controller.model.room;

import lombok.*;

@Value
@Builder
public class RoomDto {
    Long id;
    int roomNumber;
    int numberOfPersons;
    int standard;
    Long hotelId;
}
