package com.project.hotelBooking.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HotelWithRoomsDto {
    private Long id;
    private String name;
    private int numberOfStars;
    private String hotelChain;
    private Long localizationId;
    private List<RoomDto> rooms;
}
