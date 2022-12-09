package com.project.hotelBooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {
    private Long id;
    private int roomNumber;
    private int numberOfPersons;
    private int standard;
    private Long hotelId;
}
