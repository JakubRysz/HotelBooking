package com.project.hotelBooking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDto {
    private Long id;
    private int roomNumber;
    private int numberOfPersons;
    private int standard;
    private Long hotelId;
}
